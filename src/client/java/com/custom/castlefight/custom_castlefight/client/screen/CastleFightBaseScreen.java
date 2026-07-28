package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.StorageUpdateListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public abstract class CastleFightBaseScreen extends Screen implements StorageUpdateListener {
    private Map<MatchUtilities.MatchData,Boolean> needUpdate;

    protected CastleFightBaseScreen(Text title) {
        super(title);
        needUpdate = new HashMap<>();
        for (MatchUtilities.MatchData data: MatchUtilities.MatchData.values()){
            needUpdate.put(data,false);
        }
    }

    @Override
    protected void init() {
        CLIENT_TEMP.subscribe(this);
        super.init();
    }

    @Override
    public void removed() {
        CLIENT_TEMP.unsubscribe(this);
        super.removed();
    }

    @Override
    public abstract void onStorageUpdate(MatchUtilities.MatchData data);
}
