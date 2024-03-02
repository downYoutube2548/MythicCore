package com.dev.mythiccore.mythic.mechanics.apply;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.targeters.CustomTargeter;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Collection;

public class PullToLocation implements ITargetedEntitySkill {

    private final String center;
    private final PlaceholderDouble velocity;

    public PullToLocation(MythicLineConfig config) {
        this.velocity = config.getPlaceholderDouble(new String[]{"velocity", "v"}, 1.0);
        this.center = config.getString(new String[]{"center"}, "@SelfLocation");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        double velocity = this.velocity.get(data, target) / 10.0;
        Location l = BukkitAdapter.adapt((AbstractLocation) getEntityTargeter(data, this.getPlugin().getSkillManager().getTargeter(center)));

        Entity t = BukkitAdapter.adapt(target);
        double distance = l.distance(t.getLocation());
        double modxz = distance * 0.5 * velocity;
        double mody = distance * 0.34 * velocity;
        mody = l.getY() - target.getLocation().getY() != 0.0 ? mody * Math.abs(l.getY() - target.getLocation().getY()) * 0.5 : mody;
        Vector v = t.getLocation().toVector().subtract(l.toVector()).normalize().multiply(velocity);
        v.setX(v.getX() * -1.0 * modxz);
        v.setZ(v.getZ() * -1.0 * modxz);
        v.setY(v.getY() * -1.0 * mody);
        if (v.length() > 4.0) {
            v = v.normalize().multiply(4);
        }

        if (Double.isNaN(v.getX())) {
            v.setX(0);
        }

        if (Double.isNaN(v.getY())) {
            v.setY(0);
        }

        if (Double.isNaN(v.getZ())) {
            v.setZ(0);
        }

        t.setVelocity(v);
        return SkillResult.SUCCESS;
    }

    private Collection<AbstractLocation> getEntityTargeter(SkillMetadata data, SkillTargeter targeter) {

        if (targeter instanceof CustomTargeter && ((CustomTargeter)targeter).getTargeter().isPresent()) {
            targeter = ((CustomTargeter)targeter).getTargeter().get();
        }

        if (targeter instanceof ILocationSelector) {
            return ((ILocationSelector)targeter).getLocations(data);
        }

        return Sets.newHashSet();
    }
}
