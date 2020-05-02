package com.vicmatskiv.weaponlib.mission;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMissionCapability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MissionOffering {
    
    public static final long DEFAULT_MAX_DURATION = 24000 * 30;
    public static final long DEFAULT_COOLDOWN_TIME = 24000 * 1000;
    
    private static Map<String, MissionOffering> allOfferings = new HashMap<>();
    private static Map<UUID, MissionOffering> allOfferingsById = new HashMap<>();
    
    private static void registerOffering(MissionOffering offering) {
        allOfferings.put(offering.getMissionName(), offering);
        allOfferingsById.put(offering.getId(), offering);
    }
    
    public static MissionOffering getOffering(UUID offeringId) {
        return allOfferingsById.get(offeringId);
    }
    
    public static enum Level {
        EASY,
        MEDIUM,
        HARD
    }
    
    public static interface Requirement {
        public boolean isSatisfied(EntityPlayer player);
    }
    
    public static class NoRequirement implements Requirement {

        @Override
        public boolean isSatisfied(EntityPlayer player) {
            return true;
        }
    }
    
    public class CooldownMissionRequirement implements Requirement {

        @Override
        public boolean isSatisfied(EntityPlayer player) {
            Set<Mission> missions = CompatibleMissionCapability.getMissions(player);
            
            boolean isSatisfied = true;
//            System.out.println("Total world time: " + compatibility.world(player).getTotalWorldTime());
//            for(Mission m: missions) {
//                if(m.getMissionOfferingId().equals(MissionOffering.this.getId())) {
//                    if(m.getEndTime() + MissionOffering.this.getCooldownTime() < compatibility.world(player).getTotalWorldTime()) {
////                        System.out.println("Mission " + m + " with goal" + m.getGoals().get(0) + " meets cooldown requirement");
//                    } else {
//                        long timeLeft = m.getEndTime() + MissionOffering.this.getCooldownTime() - compatibility.world(player).getTotalWorldTime();
////                        System.out.println("Mission " + m + " does not meet cooldown requirement, time left: " + timeLeft);
//                    }
//                }
//            }
            isSatisfied &= missions.stream().allMatch(m -> 
                !m.getMissionOfferingId().equals(MissionOffering.this.getId())
                ||(m.getEndTime() + MissionOffering.this.getCooldownTime() < compatibility.world(player).getTotalWorldTime()));
            return isSatisfied;
        }
    }
    
    public static class NoMissionsInProgressRequirement implements Requirement {

        @Override
        public boolean isSatisfied(EntityPlayer player) {
            Set<Mission> missions = CompatibleMissionCapability.getMissions(player);
            return missions.stream().allMatch(m -> m.isCompleted(player) 
                    || m.isExpired(compatibility.world(player).getTotalWorldTime()));
        }
        
    }
    
    private static class CompositeRequirement implements Requirement {
        
        private Collection<Requirement> requirements;
        
        public CompositeRequirement() {
            this.requirements = new ArrayList<>();
        }

        @Override
        public boolean isSatisfied(EntityPlayer player) {
            return requirements.stream().allMatch(r -> r.isSatisfied(player));
        }
        
    }
    
    public static class CompletedMissionRequirement implements Requirement {
        private boolean all;
        private Collection<String> requiredMissionNames;
        
        private CompletedMissionRequirement(Collection<String> requiredMissionNames, boolean all) {
            this.all = all;
            this.requiredMissionNames = Collections.unmodifiableCollection(requiredMissionNames);
        }

        @Override
        public boolean isSatisfied(EntityPlayer player) {
            Set<Mission> missions = CompatibleMissionCapability.getMissions(player);
            
            boolean completed = true;
            for(String requiredMissionName: requiredMissionNames) {
                MissionOffering offering = allOfferings.get(requiredMissionName);
                if(offering != null) {
                    Mission matchingMission = null;
                    for(Mission mission: missions) {
                        if(mission.getMissionOfferingId().equals(offering.getId())) {
                            matchingMission = mission;
                            break;
                        }
                    }
                    completed &= matchingMission != null && matchingMission.isCompleted(player)
                            && !matchingMission.isExpired(compatibility.world(player).getTotalWorldTime());
                    if(completed && !all) {
                        break;
                    }
                } else {
                    System.err.print("Invalid mission name: " + requiredMissionName);
                }
                
            }
            return completed;
        }
    }

    public static class Builder {
        
        private static final String SHA1PRNG_ALG = "SHA1PRNG";
        
        private UUID id;
        private String missionName;
        private String missionDescription = "";
        private Level level = Level.EASY;
        private List<Supplier<Goal>> goals = new ArrayList<>();
        private List<Supplier<ItemStack>> rewardSuppliers = new ArrayList<>();

        private List<ItemStack> sampleRewards;
        private long cooldownTime = DEFAULT_COOLDOWN_TIME;
        private boolean isConcurrent;
        private CompositeRequirement requirement = new CompositeRequirement();
        private long maxDuration = DEFAULT_MAX_DURATION;

        public Builder(String missionName) {
            this.missionName = missionName;
        }
        
        private static UUID getUuid(String seed) {
            try {
                SecureRandom random = SecureRandom.getInstance(SHA1PRNG_ALG);
                random.setSeed(seed.getBytes());
                return new UUID(random.nextLong(), random.nextLong());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Cannot generate unique id", e);
            }
        }
        
        public Builder withMissionDescription(String missionDescription) {
            this.missionDescription = missionDescription;
            return this;
        }
        
        public Builder withLevel(Level level) {
            this.level = level;
            return this;
        }
        
        public Builder withGoal(Action action, int quantity) {
            goals.add(() -> new Goal(action, quantity));
            return this;
        }
        
        public Builder withReward(Item reward) {
            rewardSuppliers.add(() -> new ItemStack(reward));
            return this;
        }
        
        public Builder withReward(Item reward, int count) {
            rewardSuppliers.add(() -> new ItemStack(reward, count));
            return this;
        }
        
        public Builder requiresAny(String...requiredMissionNames) {
            this.requirement.requirements.add(new CompletedMissionRequirement(Arrays.asList(requiredMissionNames), false));
            return this;
        }
        
        public Builder requiresAll(String...requiredMissionNames) {
            this.requirement.requirements.add(new CompletedMissionRequirement(Arrays.asList(requiredMissionNames), true));
            return this;
        }
        
        public Builder withCooldownTime(int cooldownTime) {
            this.cooldownTime = cooldownTime;
            return this;
        }
        
        public Builder allowConcurrent() {
            this.isConcurrent = true;
            return this;
        }
        
        public Builder withMaxDuration(long maxDuration) {
            this.maxDuration = maxDuration;
            return this;
        }
        
        public MissionOffering build(String modId) {
            id = getUuid(modId + ":" + missionName);
            sampleRewards = rewardSuppliers.stream().map(s -> s.get()).collect(Collectors.toList());

            if(!isConcurrent) {
                this.requirement.requirements.add(new NoMissionsInProgressRequirement());
            }
            
            MissionOffering offering = new MissionOffering(this);
            this.requirement.requirements.add(offering.new CooldownMissionRequirement());
            registerOffering(offering);
            return offering;
        }
    }
    
    private Builder builder;
    
    private MissionOffering(Builder builder) {
        this.builder = builder;
    }
    
    public UUID getId() {
        return builder.id;
    }
    
    public String getMissionName() {
        return builder.missionName;
    }

    public String getMissionDescription() {
        return builder.missionDescription;
    }

    public Level getLevel() {
        return builder.level;
    }
    
    public List<ItemStack> createRewards() {
        return builder.rewardSuppliers.stream().map(s -> s.get()).collect(Collectors.toList());
    }
    
    List<ItemStack> getSampleRewards() {
        return builder.sampleRewards;
    }

    public List<Goal> createGoals() {
        return builder.goals.stream().map(s -> s.get()).collect(Collectors.toList());
    }
    
//    public Requirement getRequirement() {
//        return builder.requirement;
//    }
    
    public boolean isAvailableFor(EntityPlayer player) {
        return builder.requirement.isSatisfied(player);
    }

    public long getMaxDuration() {
        return builder.maxDuration;
    }
    
    public long getCooldownTime() {
        return builder.cooldownTime;
    }
}
