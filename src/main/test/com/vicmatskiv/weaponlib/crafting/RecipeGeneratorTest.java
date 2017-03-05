package com.vicmatskiv.weaponlib.crafting;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class RecipeGeneratorTest {

    @Test
    public void test3() throws NoSuchAlgorithmException {
        RecipeGenerator generator = new RecipeGenerator();
        
//        OptionsMetadata optionMetadata  = new OptionsMetadata.OptionMetadataBuilder()
//                .withSlotCount(9)
//                .withOption(OptionsMetadata.EMPTY_OPTION, 0, 1)
//                .withOption("red", 1, 3)
//                .withOption("blue", 1, 2)
//                .withOption("green", 1, 2)
//                .withOption("yellow", 1, 3)
//                .build();
        
        OptionsMetadata optionMetadata  = new OptionsMetadata.OptionMetadataBuilder()
                .withSlotCount(9)
                .build(CraftingComplexity.LOW, "red", "green", "blue", "yellow", "purple");
        
        List<String> values = new ArrayList<>();
        
        for(int j = 0; j < 100; j++) {
            String o = "obj" + j;
            values.add(generator.createShapedRecipe(o, optionMetadata).toString());
        }
        
        values.sort(null);
        for(String value: values) {
            System.out.format("%s\n", value);
        }
    }
    
//    public static List<Object> createShapedRecipe(List<Object> sequence, OptionsMetadata metadata) {
//    	LinkedHashMap<Object, Character> encodingMap = new LinkedHashMap<>();
//    	char startFrom = 65;
//		for(OptionMetadata optionMetadata: metadata.getMetadata()) {
//			char code;
//			if(optionMetadata.getOption() == OptionsMetadata.EMPTY_OPTION) {
//				code = ' ';
//			} else {
//				code = startFrom++;
//			}
//			encodingMap.put(optionMetadata.getOption(), code);
//		}
//		
//		List<Object> output = new ArrayList<>();
//		StringBuilder builder = new StringBuilder();
//		int i = 0;
//		for(Object t: sequence) {
//			builder.append(encodingMap.get(t));
//			if(++i % 3 == 0) {
//				output.add(builder.toString());
//				builder.setLength(0);
//			}
//		}
//		encodingMap.entrySet().stream().filter(e -> e.getKey() != OptionsMetadata.EMPTY_OPTION)
//			.forEach(e -> { output.add(e.getValue()); output.add(e.getKey());});
//		
//		return output;
//	}
//    

}
