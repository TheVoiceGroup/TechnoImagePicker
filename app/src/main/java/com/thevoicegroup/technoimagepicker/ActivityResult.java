package com.thevoicegroup.technoimagepicker;

import android.content.Intent;

public interface ActivityResult {
    void onImageActivityResult(int RequestCode, int ResultCode, Intent intent);
}
