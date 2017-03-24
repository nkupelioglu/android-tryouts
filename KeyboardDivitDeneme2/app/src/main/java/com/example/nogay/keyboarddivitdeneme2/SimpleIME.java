package com.example.nogay.keyboarddivitdeneme2;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class SimpleIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;

    private Keyboard keyboardPlate;
    private Keyboard keyboardQwerty;
    private Keyboard keyboardNum;
    private Keyboard CurrentKeyboard;
    private Keyboard keyboardCapsed;

    private boolean caps = false;

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }

    @Override
    public View onCreateInputView() {
            keyboardQwerty = new Keyboard(this, R.xml.turkceqwerty);
            keyboardPlate = new Keyboard(this, R.xml.qwerty);
            keyboardNum = new Keyboard(this, R.xml.numkeyboard);
            keyboardCapsed = new Keyboard(this, R.xml.capsedkeyboard);
            kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
            kv.setKeyboard(keyboardQwerty);
            kv.setOnKeyboardActionListener(this);
            kv.setPreviewEnabled(false);
            return kv;
    }

    private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
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
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                CurrentKeyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                //TODO: Replace this with something proper.
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }
    }

    @Override
    public void onInitializeInterface() {
        keyboardQwerty = new Keyboard(this, R.xml.turkceqwerty);
        keyboardPlate = new Keyboard(this, R.xml.qwerty);
        keyboardNum = new Keyboard(this, R.xml.numkeyboard);
        keyboardCapsed = new Keyboard(this, R.xml.capsedkeyboard);
    }


    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        attribute = getCurrentInputEditorInfo();

        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
                CurrentKeyboard = keyboardNum;
                break;

            case InputType.TYPE_CLASS_PHONE:
                CurrentKeyboard = keyboardNum;
                caps = true;
                break;

            case InputType.TYPE_CLASS_TEXT:
                caps = false;
                CurrentKeyboard = keyboardQwerty;
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;

                if (variation == InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS) {
                    CurrentKeyboard = keyboardPlate;
                    caps = true;
                }
                break;

            default:
                caps = false;
                CurrentKeyboard = keyboardQwerty;
        }

    }

    @Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);

        kv.setKeyboard(CurrentKeyboard);
        kv.setOnKeyboardActionListener(this);
    }
    @Override public void onFinishInput() {
        super.onFinishInput();

        CurrentKeyboard = keyboardQwerty;
        if (kv != null) {
            kv.closing();
        }
    }

}