package com.dev.mythiccore.aura;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.element.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The data set of specified entity about Elemental Inflection
 */
public class AuraData {
    private final UUID uuid;
    private final Map<String, AuraGauge> mapAura;
    public AuraData(UUID uuid) {
        this.uuid = uuid;
        this.mapAura = new HashMap<>();
    }

    public Map<String, AuraGauge> getMapAura() {
        return mapAura;
    }

    /**
     * Get an inflected element icon
     * @return String inflected element icon
     */
    public String getAuraIcon() {
        StringBuilder sb = new StringBuilder();
        for (String auraID : mapAura.keySet()) {

            String auraIcon;
            Element element = MythicLib.plugin.getElements().get(auraID);
            double progress = mapAura.get(auraID).getDuration() / (mapAura.get(auraID).getGaugeUnit() * ConfigLoader.getDecayRate(mapAura.get(auraID).getDecayRate())) * 100;
            if (element != null) {
                auraIcon = element.getColor()+element.getLoreIcon()+" "+Utils.progressBar(progress, 20, '▌', mapAura.get(auraID).getGaugeUnit())+element.getColor()+" "+mapAura.get(auraID).getDecayRate()+"\n";
            } else {
                auraIcon = ConfigLoader.getSpecialAuraColor(auraID)+ConfigLoader.getSpecialAuraIcon(auraID)+" "+Utils.progressBar(progress, 20, '▌', mapAura.get(auraID).getGaugeUnit())+ConfigLoader.getSpecialAuraColor(auraID)+" "+mapAura.get(auraID).getDecayRate()+"\n";
            }

            sb.append(Utils.colorize(auraIcon+"&r"));
        }
        return sb.toString();
    }

    public void reduceAura(String element, double gauge_unit) {
        if (!this.mapAura.containsKey(element)) return;
        reduceAura(element, (long)(Math.floor(gauge_unit * ConfigLoader.getDecayRate(this.mapAura.get(element).getDecayRate()))));
    }

    public void addAura(String aura, double gauge_unit, String decay_rate) {
        if (MythicLib.plugin.getElements().get(aura) == null && ConfigLoader.getSpecialAuraIcon(aura) == null) return;


        if (!this.mapAura.containsKey(aura)) {
            this.mapAura.put(aura, new AuraGauge((long) Math.floor(gauge_unit * ConfigLoader.getDecayRate(decay_rate)), gauge_unit, decay_rate));
        } else {
            String old_decay_rate = this.mapAura.get(aura).getDecayRate();
            this.mapAura.put(aura, new AuraGauge((long) Math.floor(gauge_unit * ConfigLoader.getDecayRate(old_decay_rate)), gauge_unit, old_decay_rate));
        }


        if (!MythicCore.getAuraManager().entityAura.containsKey(this.uuid)) MythicCore.getAuraManager().entityAura.put(this.uuid, this);
    }

    protected void setAura(String aura, long duration, double gauge_unit, String decay_rate) {
        if (MythicLib.plugin.getElements().get(aura) == null && ConfigLoader.getSpecialAuraIcon(aura) == null) return;
        this.mapAura.put(aura, new AuraGauge(duration, gauge_unit, decay_rate));
        if (!MythicCore.getAuraManager().entityAura.containsKey(this.uuid)) MythicCore.getAuraManager().entityAura.put(this.uuid, this);
    }

    public void removeAura(String aura) {
        if (MythicLib.plugin.getElements().get(aura) == null && ConfigLoader.getSpecialAuraIcon(aura) == null) return;
        if (!this.mapAura.containsKey(aura)) return;

        this.mapAura.remove(aura);

        if (mapAura.isEmpty()) MythicCore.getAuraManager().entityAura.remove(this.uuid);
    }

    protected void reduceAura(String aura, long duration) {
        if (MythicLib.plugin.getElements().get(aura) == null && ConfigLoader.getSpecialAuraIcon(aura) == null) return;
        if (!this.mapAura.containsKey(aura)) return;

        if (this.mapAura.get(aura).getDuration() <= duration) {
            removeAura(aura);
        } else {
            setAura(aura, this.mapAura.get(aura).getDuration() - duration, this.mapAura.get(aura).getGaugeUnit(), this.mapAura.get(aura).getDecayRate());
        }
    }
}
