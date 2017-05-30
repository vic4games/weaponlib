package com.vicmatskiv.weaponlib.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationManager {

    private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);


    private static final float DROP_BLOCK_COEFFICIENT_MIN = 0f;
    private static final float DROP_BLOCK_COEFFICIENT_MAX = 5f;

    private static final float EXPLOSION_COEFFICIENT_MAX = 2f;
    private static final float EXPLOSION_COEFFICIENT_MIN = 0.2f;

    private static final float BLEEDING_ON_HIT_COEFFICIENT_MIN = 0f;
    private static final float BLEEDING_ON_HIT_COEFFICIENT_MAX = 1f;

    private static final int ORE_MIN_PER_CHUNK = 0;
    private static final int ORE_MAX_PER_CHUNK = 50;

    //private static final Float DEFAULT_DAMAGE_COEFFICIENT = 1.0f;

    private static final Predicate<Ore> DEFAULT_ORE_VALIDATOR = ore -> ore.spawnsPerChunk >= ORE_MIN_PER_CHUNK
            && ore.spawnsPerChunk <= ORE_MAX_PER_CHUNK;

    private static final Predicate<Explosions> DEFAULT_EXPLOSIONS_VALIDATOR = explosions ->
        explosions != null;


    public static final class Builder {



        public Map<String, Ore> ores = new HashMap<>();

        private Source defaultConfigSource;
        private Source userConfigSource;
        private File userConfigFile;

        private Predicate<Ore> oreValidator = DEFAULT_ORE_VALIDATOR;
        private Predicate<Explosions> explosionsValidator = DEFAULT_EXPLOSIONS_VALIDATOR;

        public Builder withDefaultConfiguration(Source defaultConfigSource) {
            this.defaultConfigSource = defaultConfigSource;
            return this;
        }

        Builder withUserConfiguration(Source userConfigSource) {
            this.userConfigSource = userConfigSource;
            return this;
        }

        public Builder withUserConfiguration(File userConfigFile) {
            this.userConfigFile = userConfigFile;
            this.userConfigSource = new StreamSource(userConfigFile);
            return this;
        }

        public Builder withOreValidator(Predicate<Ore> oreValidator) {
            this.oreValidator = oreValidator;
            return this;
        }

        public Builder withExplosionsValidator(Predicate<Explosions> explosionsValidator) {
            this.explosionsValidator = explosionsValidator;
            return this;
        }

        public ConfigurationManager build() {
            Configuration defaultUpdatableConfig = createConfiguration(defaultConfigSource);
            Configuration userConfig = createConfiguration(userConfigSource);
            return new ConfigurationManager(merge(userConfig, defaultUpdatableConfig), userConfigFile);
        }

        static Configuration createConfiguration(Source source) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Configuration> configElement = jaxbUnmarshaller.unmarshal(source, Configuration.class);
                return configElement.getValue();
            } catch (JAXBException e) {
                logger.error("Failed to parse configuration: " + e, e);
                return null;
            }
        }

        private Configuration merge(Configuration userConfig, Configuration defaultUpdatableConfig) {
            if(userConfig == null) {
                return defaultUpdatableConfig;
            }
            mergeOres(userConfig, defaultUpdatableConfig);
            mergeExplosions(userConfig, defaultUpdatableConfig);
            mergeProjectiles(userConfig, defaultUpdatableConfig);
            return defaultUpdatableConfig;
        }

        private void mergeExplosions(Configuration userConfig, Configuration defaultUpdatableConfig) {
            if(explosionsValidator.test(userConfig.getExplosions())) {

                Float userDamageCoefficient = userConfig.getExplosions().getDamage();
                if (userDamageCoefficient == null) {
                    userDamageCoefficient = defaultUpdatableConfig.getExplosions().getDamage();
                } else if (userDamageCoefficient < EXPLOSION_COEFFICIENT_MIN) {
                    userDamageCoefficient = EXPLOSION_COEFFICIENT_MIN;
                } else if (userDamageCoefficient > EXPLOSION_COEFFICIENT_MAX) {
                    userDamageCoefficient = EXPLOSION_COEFFICIENT_MAX;
                }
                defaultUpdatableConfig.getExplosions().setDamage(userDamageCoefficient);

                Float userDropBlockChance = userConfig.getExplosions().getDropBlockChance();
                if (userDropBlockChance == null) {
                    userDropBlockChance = defaultUpdatableConfig.getExplosions().getDropBlockChance();
                } else if (userDropBlockChance < DROP_BLOCK_COEFFICIENT_MIN) {
                    userDropBlockChance = DROP_BLOCK_COEFFICIENT_MIN;
                } else if (userDropBlockChance > DROP_BLOCK_COEFFICIENT_MAX) {
                    userDropBlockChance = DROP_BLOCK_COEFFICIENT_MAX;
                }
                defaultUpdatableConfig.getExplosions().setDropBlockChance(userDropBlockChance);
            }
        }

        private void mergeProjectiles(Configuration userConfig, Configuration defaultUpdatableConfig) {
            if(userConfig != null) {
                if(userConfig.getProjectiles() != null) {
                    Float bleedingOnHit = userConfig.getProjectiles().getBleedingOnHit();
                    if(bleedingOnHit != null) {
                        if(bleedingOnHit < BLEEDING_ON_HIT_COEFFICIENT_MIN) {
                            bleedingOnHit = BLEEDING_ON_HIT_COEFFICIENT_MIN;
                        } else if(bleedingOnHit > BLEEDING_ON_HIT_COEFFICIENT_MAX) {
                            bleedingOnHit = BLEEDING_ON_HIT_COEFFICIENT_MAX;
                        }
                        defaultUpdatableConfig.getProjectiles().setBleedingOnHit(bleedingOnHit);
                    }
                    if(userConfig.getProjectiles().isDestroyGlassBlocks() != null) {
                        defaultUpdatableConfig.getProjectiles().setDestroyGlassBlocks(
                                userConfig.getProjectiles().isDestroyGlassBlocks());
                    }
                }
            }
        }

        private void mergeOres(Configuration userConfiguration, Configuration updatableDefaults) {
            if(userConfiguration.getOres() != null) {
                updatableDefaults.getOres().getOre().forEach(updatableDefaultOre -> {
                    userConfiguration.getOres().getOre().stream()
                    .filter(o -> updatableDefaultOre.getName().equalsIgnoreCase(o.getName()))
                    .findFirst()
                    .ifPresent(userOre -> mergeOre(userOre, updatableDefaultOre));
                });
            }
        }

        private void mergeOre(Ore userOre, Ore updatableDefaultOre) {
            if(oreValidator.test(userOre)) {
                updatableDefaultOre.spawnsPerChunk = userOre.spawnsPerChunk;
            }
        }
    }

    private Configuration config;
    private File userConfigFile;

    protected ConfigurationManager(Configuration config, File userConfigFile) {
        this.config = config;
        this.userConfigFile = userConfigFile;
    }

    protected Configuration getConfiguration() {
        return config;
    }

    public void save() {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            if(userConfigFile != null) {
                marshaller.marshal(new ObjectFactory().createConfiguration(config), userConfigFile);
            } else {
                marshaller.marshal(new ObjectFactory().createConfiguration(config), System.out);
            }

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public Explosions getExplosions() {
        return config.getExplosions();
    }

    public Ore getOre(String oreName) {
        Ores ores = config.getOres();
        if(ores == null) {
            return null;
        }
        return ores.getOre().stream()
                .filter(o -> oreName.equalsIgnoreCase(o.getName()))
                .findFirst()
                .orElse(null);
    }

    public Projectiles getProjectiles() {
        return config.getProjectiles();
    }

}
