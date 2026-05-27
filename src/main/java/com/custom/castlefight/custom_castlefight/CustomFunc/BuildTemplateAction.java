package com.custom.castlefight.custom_castlefight.CustomFunc;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class BuildTemplateAction {
    private String race,name;
    private BuildAction action;
    private BuildFunc.BuildTemplate newBuild,oldBuild;
    private int level;

    public enum BuildAction{
        GET_ALL_RACES,
        GET_BUILDS_SET,
        GET_BUILD,
        GET_LEVELS_SET,
        REMOVE_BUILD,
        REMOVE_RACE,
        REMOVE_LEVEL,
        PUT_BUILD,
        PUT_RACE,
        REPLACE_BUILD,
        GET_BUIDS_SET_LEVEL,
    }
    public void write(RegistryByteBuf buf){
        buf.writeBoolean(this.race != null);
        if(this.race != null)buf.writeString(this.race);
        buf.writeBoolean(this.name != null);
        if(this.name != null) buf.writeString(this.name);
        buf.writeBoolean(this.action != null);
        if(this.action != null)buf.writeEnumConstant(this.action);
        buf.writeBoolean(this.newBuild != null);
        if(this.newBuild != null)this.newBuild.write(buf);
        buf.writeBoolean(this.oldBuild != null);
        if(this.oldBuild != null)this.oldBuild.write(buf);
        buf.writeInt(this.level);
    }

    public BuildAction getAction() {
        return this.action;
    }
    public String getRace() {
        return this.race;
    }
    public String getName() {
        return this.name;
    }
    public int getLevel() {
        return this.level;
    }
    public BuildFunc.BuildTemplate getNewBuild() {
        return this.newBuild;
    }
    public BuildFunc.BuildTemplate getOldBuild() {
        return this.oldBuild;
    }

    public static PacketCodec<RegistryByteBuf,BuildTemplateAction> PACKET_CODEC = PacketCodec.of(
            ((value, buf) -> value.write(buf)),
            BuildTemplateAction::read
    );
    public static BuildTemplateAction read(RegistryByteBuf buf){
        BuildTemplateAction newAction = new BuildTemplateAction();
        if (buf.readBoolean())newAction.setRace(buf.readString());
        if (buf.readBoolean())newAction.setName(buf.readString());
        if(buf.readBoolean())newAction.action = buf.readEnumConstant(BuildAction.class);
        if (buf.readBoolean())newAction.setNewBuild(BuildFunc.BuildTemplate.read(buf));
        if (buf.readBoolean())newAction.setOldBuild(BuildFunc.BuildTemplate.read(buf));
        newAction.setLevel(buf.readInt());
        return newAction;
    }

    public void setRace(String race){
        this.race = BuildFunc.BuildTemplate.normalizeName(race);
    }
    public void setName(String name){
        this.name = BuildFunc.BuildTemplate.normalizeName(name);
    }
    public void setLevel(int level){
        this.level = level;
    }
    public void setNewBuild(BuildFunc.BuildTemplate newBuild) {
        this.newBuild = newBuild;
    }
    public void setOldBuild(BuildFunc.BuildTemplate oldBuild) {
        this.oldBuild = oldBuild;
    }
    public void setBuild(BuildFunc.BuildTemplate build){ this.newBuild = build; }

    public void setActionPutBuild(){
        this.action = BuildAction.PUT_BUILD;
    }
    public void setActionPutRace(){
        this.action = BuildAction.PUT_RACE;
    }
    public void setActionRemoveBuild(){
        this.action = BuildAction.REMOVE_BUILD;
    }
    public void setActionRemoveRace(){
        this.action = BuildAction.REMOVE_RACE;
    }
    public void setActionRemoveLevel(){ this.action = BuildAction.REMOVE_LEVEL; }
    public void setActionGetBuild(){ this.action = BuildAction.GET_BUILD; }
    public void setActionGetLevelsSet(){ this.action = BuildAction.GET_LEVELS_SET; }
    public void setActionGetBuildsSet(){ this.action = BuildAction.GET_BUILDS_SET; }
    public void setActionReplaceBuild(){ this.action = BuildAction.REPLACE_BUILD; }
    public void setActionGetAllRaces() { this.action = BuildAction.GET_ALL_RACES;}
    public void setActionGetBuildsSetLevelN() { this.action = BuildAction.GET_BUIDS_SET_LEVEL; }

    private boolean hasRace() {
        return this.race != null && !this.race.isEmpty();
    }

    private boolean hasName() {
        return this.name != null && !this.name.isEmpty();
    }

    private boolean hasLevel() {
        return this.level > 0;
    }
    private boolean hasNewBuild() {
        return this.newBuild != null;
    }
    private boolean hasOldBuild() {
        return this.oldBuild != null;
    }

    public boolean can(){
       return switch (this.action){
            case GET_BUILDS_SET, REMOVE_RACE, PUT_RACE -> (hasRace()
                    || hasNewBuild());
           case GET_BUILD, REMOVE_LEVEL -> ((hasRace() && hasName() && hasLevel())
                    || hasNewBuild());
            case GET_LEVELS_SET, REMOVE_BUILD -> (hasRace() && hasName()
                    || hasNewBuild());
           case PUT_BUILD -> hasNewBuild();
           case REPLACE_BUILD -> (hasNewBuild() && hasOldBuild());
           case GET_BUIDS_SET_LEVEL -> (hasRace() && hasLevel()) || hasNewBuild();
           case GET_ALL_RACES -> true;
           case null, default -> false;
       };

    }

}
