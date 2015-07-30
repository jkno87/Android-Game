package com.jgame.game;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class LoadActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getBooleanExtra("EXIT", false)){
            finish();
        }

        GameResources.gameLogic = new GameLogic();
        GameResources.soundManager = new SoundManager(this);
        GameResources.gameRenderer = new GameRenderer(GameResources.gameLogic, this.getResources());

        startActivity(new Intent(this, GameActivity.class));

    }
}
