package com.marcn.mediathek.model.base;

import android.support.annotation.Nullable;

public interface Video {
    @Nullable
    String getUrl();

    @Nullable
    String getThumbnailUrl();
}
