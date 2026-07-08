package com.custom.castlefight.custom_castlefight.CustomFunc;

import net.minecraft.network.RegistryByteBuf;

public class MapUtilities {
    public static class GamingMap {
        private String mapName;

        public void write(RegistryByteBuf buf){
            buf.writeString(mapName);
        }
        public static GamingMap read(RegistryByteBuf buf){
            return new GamingMap(buf.readString());
        }
        public GamingMap(String name){
            this.mapName = name;
        }
    }
}
