package com.danny.aptdemo;

import android.util.Log;

import com.danny.annotations.Whitehole;

/**
 * Created by Danny 姜
 */
@Whitehole("WormholeMiddle")
class InterfaceImplementation implements InterfaceDeclaration{

    @Override
    public void interfaceTest() {
        Log.i("TAG", "interfaceTest: ");
    }
}
