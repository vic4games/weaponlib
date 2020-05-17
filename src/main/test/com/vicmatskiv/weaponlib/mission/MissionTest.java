package com.vicmatskiv.weaponlib.mission;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class MissionTest {

    @Test
    public void test() throws FileNotFoundException {
        MissionOffering offering = Missions.parse("src/main/test/mission.json");
        System.out.println(offering);
    }
    
    @Test
    public void testEntityMissionOfferings() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
        Gson gson = new Gson();
        Map<?, ?> result = gson.fromJson(new FileReader("src/main/test/entity_mission_offerings.json"), Map.class);
        System.out.println(result);
    }
}
