package kr.ac.anyang.chp04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

/**
 * Created by Administrator on 2018-04-09.
 */

public class GameThread extends Thread {
    private boolean m_Run = false;
    private SurfaceHolder m_SurfaceHolder;
    private GameSurfaceView m_GameSurfaceView;

    public GameThread(SurfaceHolder surfaceHolder, GameSurfaceView gameView) {
        this.m_SurfaceHolder = surfaceHolder;
        this.m_GameSurfaceView = gameView;
    }

    public void setRunning(boolean b) {
        this.m_Run = b;
    }

    @Override
    public void run() {
        Canvas canvas;
        while (this.m_Run) {
            canvas = null;
            try {
                canvas = this.m_SurfaceHolder.lockCanvas(null);
                canvas.drawColor(Color.WHITE);
                synchronized (this.m_SurfaceHolder) {
                    this.m_GameSurfaceView.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    this.m_SurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
