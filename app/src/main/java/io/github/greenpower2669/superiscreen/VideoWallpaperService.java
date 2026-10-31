package io.github.greenpower2669.superiscreen;

import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;

public final class VideoWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    private final class VideoEngine extends Engine {
        private MediaPlayer player;
        private boolean visible = false;
        private boolean surfaceReady = false;
        private boolean prepared = false;

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            surfaceReady = true;
            startIfPossible();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            surfaceReady = true;
            startIfPossible();
        }

        @Override
        public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            if (visible) {
                startIfPossible();
            } else if (player != null && prepared && player.isPlaying()) {
                player.pause();
            }
        }

        private void startIfPossible() {
            if (!visible || !surfaceReady) return;
            if (player != null) {
                if (prepared && !player.isPlaying()) player.start();
                return;
            }

            File video = new File(getFilesDir(), MainActivity.VIDEO_NAME);
            if (!video.isFile()) return;

            try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                player = mediaPlayer;
                prepared = false;
                mediaPlayer.setDataSource(video.getAbsolutePath());
                mediaPlayer.setSurface(getSurfaceHolder().getSurface());
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(0f, 0f);
                mediaPlayer.setOnPreparedListener(mp -> {
                    if (player == mp) {
                        prepared = true;
                        if (visible && surfaceReady) mp.start();
                    }
                });
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    releasePlayer();
                    return true;
                });
                mediaPlayer.prepareAsync();
            } catch (IOException | RuntimeException e) {
                releasePlayer();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            surfaceReady = false;
            releasePlayer();
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onDestroy() {
            releasePlayer();
            super.onDestroy();
        }

        private void releasePlayer() {
            MediaPlayer old = player;
            player = null;
            prepared = false;
            if (old != null) {
                old.setOnPreparedListener(null);
                old.setOnErrorListener(null);
                old.release();
            }
        }
    }
}
