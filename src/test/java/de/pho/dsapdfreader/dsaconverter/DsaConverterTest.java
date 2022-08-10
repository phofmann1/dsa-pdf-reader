package de.pho.dsapdfreader.dsaconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.config.TopicEnum;
import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;


class DsaConverterTest
{

    @Test
    public void testConvertTextWithMetaInfoToTrick()
    {
        List<TextWithMetaInfo> r = this.initResult();
        TopicConfiguration c = new TopicConfiguration();
        c.topic = TopicEnum.BLESSINGS;
        c.dataSize = 1100;
        c.nameSize = 1300;
        c.startAfterLine = 1;
        c.endAfterLine = 4;
        List<MysticalSkillRaw> result = new DsaConverterMysticalSkill().convertTextWithMetaInfo(r, c);
        assertEquals(1, result.size());
        assertEquals("Name", result.get(0).name);
        assertEquals("Text 1 Text 2", result.get(0).description);
    }

    @Test
    public void testConcatForDataValueConcatWithSpaceAndTrimmed()
    {
        String origin = "  Text 1 ";
        String newValue = "     Text 2  ";
        String result = DsaConverter.concatForDataValue(origin, newValue);
        assertEquals("Text 1 Text 2", result);
    }

    @Test
    public void testConcatForDataValueConcatWithDash()
    {
        String origin = "   -  Text -1- ";
        String newValue = "  Text -2-  ";
        String result = DsaConverter.concatForDataValue(origin, newValue);
        assertEquals("-  Text -1Text -2-", result);
    }

    public ArrayList<TextWithMetaInfo> initResult()
    {
        ArrayList<TextWithMetaInfo> returnValue = new ArrayList<>();

        TextWithMetaInfo l0 = new TextWithMetaInfo(
            "START",
            true,
            false,
            1600,
            "PDTrueTypeFont STCRBN+GentiumBasic-Bold",
            1,
            1,
            "TestPub"
        );

        TextWithMetaInfo l1 = new TextWithMetaInfo(
            "Name ",
            true,
            false,
            1300,
            "PDTrueTypeFont STCRBN+GentiumBasic-Bold",
            1,
            2,
            "TestPub"
        );
        TextWithMetaInfo l2 = new TextWithMetaInfo(
            " Text 1 ",
            false,
            false,
            1000,
            "PDTrueTypeFont CICZLT+GentiumBasic",
            2,
            1,
            "TestPub"
        );
        TextWithMetaInfo l3 = new TextWithMetaInfo(
            " Text 2 ",
            false,
            false,
            1000,
            "PDType0Font/PDCIDFontType2, PostScript name: FYEYJD+GentiumBasic",
            2,
            2,
            "TestPub"
        );
        TextWithMetaInfo l4 = new TextWithMetaInfo(
            "END",
            false,
            false,
            1000,
            "PDType0Font/PDCIDFontType2, PostScript name: FYEYJD+GentiumBasic",
            2,
            3,
            "TestPub"
        );
        TextWithMetaInfo l5 = new TextWithMetaInfo(
            "Text der nicht erscheinen darf",
            false,
            false,
            1000,
            "PDType0Font/PDCIDFontType2, PostScript name: FYEYJD+GentiumBasic",
            3,
            1,
            "TestPub"
        );
        returnValue.add(l0);
        returnValue.add(l1);
        returnValue.add(l2);
        returnValue.add(l3);
        returnValue.add(l4);
        returnValue.add(l5);

        return returnValue;
    }
}