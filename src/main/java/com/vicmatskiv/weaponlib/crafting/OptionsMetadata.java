package com.vicmatskiv.weaponlib.crafting;

import java.util.LinkedHashMap;

public class OptionsMetadata {
	
	public static final Object EMPTY_OPTION = new Object();
	
	public static enum Complexity {LOW, MEDIUM, HIGH};
	
	static class OptionMetadata {
		private int minOccurs;
		
		private OptionMetadata(Object option, int minOccurs, int maxOccurs) {
			this.option = option;
			this.minOccurs = minOccurs;
			this.maxOccurs = maxOccurs;
		}

		private int maxOccurs;
		private Object option;
		

		protected int getMinOccurs() {
			return minOccurs;
		}

		protected int getMaxOccurs() {
			return maxOccurs;
		}

		protected Object getOption() {
			return option;
		}
	}
    
    public static class OptionMetadataBuilder {
        LinkedHashMap<Object, OptionMetadata> optionMetadata = new LinkedHashMap<>();
        private int slotCount;
        
        public OptionMetadataBuilder withSlotCount(int slotCount) {
            this.slotCount = slotCount;
            return this;
        }
        
        public OptionMetadataBuilder withOption(int minOccurs, int maxOccurs) {
        	OptionMetadata metadata = new OptionMetadata(EMPTY_OPTION, minOccurs, maxOccurs);
            metadata.minOccurs = minOccurs;
            metadata.maxOccurs = maxOccurs;
            optionMetadata.put(EMPTY_OPTION, metadata);
            return this;
        }
        
        public OptionMetadataBuilder withOption(Object option, int minOccurs, int maxOccurs) {
        	if(minOccurs > maxOccurs) {
        		throw new IllegalArgumentException("Min occurs must be less or equals maxOccurs");
        	}
        	OptionMetadata metadata = new OptionMetadata(option, minOccurs, maxOccurs);
            metadata.minOccurs = minOccurs;
            metadata.maxOccurs = maxOccurs;
            optionMetadata.put(option, metadata);
            return this;
        }
        
        public OptionsMetadata buildSimple(Object...options) {
        	if(options.length > slotCount) {
        		throw new IllegalArgumentException("Option count must be less than slots");
        	}
        	for(Object option: options) {
        		withOption(option, 1, 1);
        	}
        	withOption(EMPTY_OPTION, 0, slotCount - options.length);
        	return build();
        }
        
        public OptionsMetadata build(Complexity complexity, Object...options) {
        	int complexityIndex = complexity.ordinal() + 1;
        	if(options.length * complexityIndex > slotCount) {
        		throw new IllegalArgumentException("Too many options for complexity level " + complexity);
        	}
        	for(Object option: options) {
        		withOption(option, complexityIndex, complexityIndex);
        	}
        	withOption(EMPTY_OPTION, 0, slotCount - options.length * complexityIndex);
        	return build();
        }
        
        public OptionsMetadata build() {
            if(slotCount == 0) {
                throw new IllegalStateException("Slot count not set");
            }
            int totalMaxOccurs = 0;
            int totalMinOccurs = 0;
            for(OptionMetadata m: optionMetadata.values()) {
                totalMaxOccurs += m.maxOccurs;
                totalMinOccurs += m.minOccurs;
            }
            if(totalMaxOccurs < slotCount) {
                throw new IllegalStateException("Total slot count is less than total max occurs");
            }
            if(totalMinOccurs > slotCount) {
                throw new IllegalStateException("Total max occurs exceeds the number of slots");
            }

			OptionMetadata[] metadata = (OptionMetadata[]) optionMetadata.entrySet().stream().map(e -> new OptionMetadata(e.getKey(), 
            		e.getValue().minOccurs, e.getValue().maxOccurs)).toArray(size -> new OptionMetadata[size]);
			
            return new OptionsMetadata(metadata);
        }
    }
    
    private OptionMetadata[] metadata;

    private OptionsMetadata(OptionMetadata[] metadata) {
        this.metadata = metadata;
    }
    
	public OptionMetadata[] getMetadata() {
        return metadata;
    }
	
	
}