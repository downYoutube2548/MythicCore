package com.dev.mythiccore.mythic.mechanics.apply;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.targeters.CustomTargeter;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class SudoSnapshot extends Snapshot implements ITargetedEntitySkill {

    private final String caster;

    public SudoSnapshot(MythicLineConfig config) {
        super(config);
        this.caster = config.getString(new String[]{"caster", "c"}, "@caster");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Collection<AbstractEntity> targets = getEntityTargeter(skillMetadata, this.getPlugin().getSkillManager().getTargeter(caster));

        for (AbstractEntity target : targets) {
            if (target instanceof Player || (target instanceof ActiveMob && super.check_owner)) {
                Player player = null;

                if (target instanceof Player p) {

                    player = p;

                } else {

                    ActiveMob am = (ActiveMob) target;

                    if (am.getOwner().isPresent()) {
                        UUID uuid = am.getOwner().get();
                        assert uuid != null;
                        Player a = Bukkit.getPlayer(uuid);
                        if (a != null) {
                            player = a;
                        }
                    }
                }

                if (player != null) {
                    PlayerData playerData = PlayerData.get(player);
                    cast(skillMetadata, target.getBukkitEntity(), playerData, abstractEntity);

                } else {
                    cast(skillMetadata, target.getBukkitEntity(), abstractEntity);
                }

            } else {
                cast(skillMetadata, target.getBukkitEntity(), abstractEntity);
            }
        }

        return SkillResult.SUCCESS;
    }

    private Collection<AbstractEntity> getEntityTargeter(SkillMetadata data, SkillTargeter targeter) {
        SkillCaster var5 = data.getCaster();
        if (var5 instanceof ActiveMob activeCaster) {
            if (targeter.isSudoParent()) {
                activeCaster.getParent().ifPresent((parent) -> {
                    data.setCaster(this.getPlugin().getSkillManager().getCaster(parent));
                });
            }

            if (targeter.isSudoOwner()) {
                activeCaster.getOwner().ifPresent((owner) -> {
                    AbstractEntity ownerEntity = MythicBukkit.inst().getBootstrap().getEntity(owner);
                    if (ownerEntity != null) {
                        data.setCaster(this.getPlugin().getSkillManager().getCaster(ownerEntity));
                    }

                });
            }

            if (targeter.isSudoTrigger() && data.getTrigger() != null) {
                data.setCaster(this.getPlugin().getSkillManager().getCaster(data.getTrigger()));
            }
        }

        if (targeter instanceof CustomTargeter && ((CustomTargeter)targeter).getTargeter().isPresent()) {
            targeter = ((CustomTargeter)targeter).getTargeter().get();
        }

        if (targeter instanceof IEntitySelector) {
            try {
                return ((IEntitySelector)targeter).getEntities(data);
            } catch (IllegalArgumentException var6) {
                return Sets.newHashSet();
            }
        } else {
            return Sets.newHashSet();
        }
    }
}
