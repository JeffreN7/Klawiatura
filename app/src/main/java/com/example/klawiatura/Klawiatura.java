package com.example.klawiatura;


import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import android.content.Context;

import android.media.MediaPlayer;

import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

import android.provider.Settings;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;



public class Klawiatura extends InputMethodService implements KeyboardView.OnKeyboardActionListener {


    private KeyboardView kv;
    private Keyboard keyboard;
    private boolean isCaps = false;

    private TextView editText;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateInputView() {
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard,null);
        keyboard = new Keyboard(this,R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }


    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }


    @Override
    public void onKey(int i, int[] ints) {

        InputConnection ic = getCurrentInputConnection();
        playClick(i);
        switch (i)
        {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1,0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));
                break;
            case 57:
                NfcManager manager2 = (NfcManager) getSystemService(Context.NFC_SERVICE);
                NfcAdapter adapter2 = manager2.getDefaultAdapter();
                if (adapter2 != null && adapter2.isEnabled()) {
                    Toast.makeText(Klawiatura.this, "NFC włączone!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Klawiatura.this, "NFC wyłączone!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            case 56:
                NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
                NfcAdapter adapter = manager.getDefaultAdapter();
                if (adapter != null && adapter.isEnabled()) {
                    Toast.makeText(Klawiatura.this, "NFC włączone!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Klawiatura.this, "NFC wyłączone!", Toast.LENGTH_LONG).show();
                }
                break;
            case 55:
                Toast.makeText(getApplicationContext(), "Uważaj co robisz!", Toast.LENGTH_SHORT).show();
                break;
            case 54:
                Context context = getApplicationContext();
                File path = context.getExternalFilesDir(null);
                File file = new File(path, "text.txt");
                FileOutputStream stream = null;
                try {
                    stream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    try {
                        stream.write("Przykładowy tekst".getBytes());
                        Toast.makeText(this, "Plik zapisano w " + getFilesDir() + "/",
                                Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } finally {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 53:
                final MediaPlayer sawSound = MediaPlayer.create(this, R.raw.samp);
                sawSound.start();
                break;
            case 51:
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);
                break;
            case 48:
                editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            default:
                char code = (char)i;
                if(Character.isLetter(code) && isCaps)
                    code = Character.toUpperCase(code);
                ic.commitText(String.valueOf(code),1);
        }
    }


    private void playClick(int i) {

        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(i)
        {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }




    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        ic.commitText(text, 0);
    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

}
