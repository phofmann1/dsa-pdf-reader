package de.pho.dsapdfreader.exporter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.extractor.Extractor;
import de.pho.dsapdfreader.exporter.model.AttributeShort;
import de.pho.dsapdfreader.exporter.model.ResistDifficulty;

public class ExtractorMysticalSkillDifficulty extends Extractor
{
    public static final Pattern PAT_DIFFICULTY = Pattern.compile(
        "(?<=\\(modifiziert um )" + // preceding: "(modifiziert um "
            ".*" +
            "(?=\\))" + // following: ")"
            "|" + // OR
            "(?<=\\(erschwert um )" + // preceding: "(erschwert um "
            ".*" +
            "(?=\\))" // following: ")"
    );

    public static ResistDifficulty retrieveDifficulty(MysticalSkillRaw msr)
    {
        ResistDifficulty returnValue = null;
        Matcher m = PAT_DIFFICULTY.matcher(msr.check);
        if (m.find())
        {
            returnValue = new ResistDifficulty();
            String difficultyString = m.group();
            List<AttributeShort> resistAttributes = new ArrayList<>();
            if (difficultyString.contains("ZK")) resistAttributes.add(AttributeShort.ZK);
            if (difficultyString.contains("SK")) resistAttributes.add(AttributeShort.SK);
            if (resistAttributes.size() > 0) returnValue.resistAttributes = resistAttributes;

            returnValue.isDifficultyByCreature = difficultyString.contains("schwierigkeit");
        }
        return returnValue;
    }
}
