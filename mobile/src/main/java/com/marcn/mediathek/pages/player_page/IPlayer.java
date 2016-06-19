package com.marcn.mediathek.pages.player_page;

public interface IPlayer {
    interface Callbacks {
        void showBottomBar();
    }

    void setCallbacks(Callbacks callbacks);

    void setQuality(int format);

    String getSelectedQuality();

    String[] getFilteredQualities();
}
