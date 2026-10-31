package io.github.greenpower2669.superiscreen;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainActivity extends Activity {
    private static final int PICK_VIDEO = 11;
    public static final String VIDEO_NAME = "wallpaper.mp4";
    private static final long MAX_BYTES = 200L * 1024L * 1024L;
    private TextView status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
        updateStatus();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(16, 24, 39));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int margin = dp(22);
        layout.setPadding(margin, margin, margin, margin);
        scroll.addView(layout);

        TextView title = text("▶ SuperIScreen", 29, Color.WHITE);
        title.setContentDescription("Super I Screen");
        layout.addView(title);

        layout.addView(text("Votre vidéo en fond d'écran Android", 19, 0xFF9FE6F8));
        layout.addView(text("Pour l'écran de verrouillage Samsung, la Galerie sait déjà appliquer directement une vidéo.", 16, Color.WHITE));

        status = text("", 16, 0xFFEEDC82);
        layout.addView(status);

        Button select = button("1 · Choisir une vidéo");
        select.setOnClickListener(v -> chooseVideo());
        layout.addView(select);

        Button install = button("2 · Prévisualiser le fond animé");
        install.setOnClickListener(v -> previewWallpaper());
        layout.addView(install);

        layout.addView(text("Samsung Galaxy : ouvrez votre vidéo dans Galerie → ⋮ → Définir comme fond d'écran → Écran de verrouillage. Selon One UI, vous pouvez aussi maintenir l'écran verrouillé, puis choisir Fonds d'écran → Galerie.", 15, 0xFFD1D5DB));
        layout.addView(text("Lecture en boucle, sans son, avec pause automatique quand l'écran animé n'est plus visible. Le choix de l'écran de verrouillage dépend d'Android et du fabricant.", 14, 0xFF9CA3AF));

        setContentView(scroll);
    }

    private TextView text(String value, int sp, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(color);
        view.setTextSize(sp);
        view.setPadding(0, dp(12), 0, dp(12));
        return view;
    }

    private Button button(String label) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setAllCaps(false);
        return btn;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void updateStatus() {
        File video = new File(getFilesDir(), VIDEO_NAME);
        status.setText(video.isFile()
                ? "Vidéo importée localement · " + (video.length() / 1024) + " Ko"
                : "Aucune vidéo sélectionnée");
    }

    private void chooseVideo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(intent, PICK_VIDEO);
        } catch (Exception e) {
            Toast.makeText(this, "Sélecteur de vidéos indisponible", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_VIDEO || resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }
        Uri uri = data.getData();
        File temp = new File(getFilesDir(), "incoming.tmp");
        File target = new File(getFilesDir(), VIDEO_NAME);
        try (InputStream in = getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(temp)) {
            if (in == null) throw new IllegalStateException("Impossible de lire la vidéo");
            byte[] buffer = new byte[64 * 1024];
            long total = 0;
            int count;
            while ((count = in.read(buffer)) != -1) {
                total += count;
                if (total > MAX_BYTES) throw new IllegalStateException("Vidéo supérieure à 200 Mo");
                out.write(buffer, 0, count);
            }
            if (total == 0) throw new IllegalStateException("Vidéo vide");
            out.flush();
            Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Toast.makeText(this, "Vidéo importée !", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            temp.delete();
            Toast.makeText(this, "Import impossible : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        updateStatus();
    }

    private void previewWallpaper() {
        if (!new File(getFilesDir(), VIDEO_NAME).isFile()) {
            Toast.makeText(this, "Choisissez d'abord une vidéo", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent preview = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        preview.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, VideoWallpaperService.class));
        try {
            startActivity(preview);
        } catch (Exception e) {
            try {
                startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
            } catch (Exception nested) {
                Toast.makeText(this, "Aucun sélecteur de fond animé disponible", Toast.LENGTH_LONG).show();
            }
        }
    }
}
