package de.pho.dsapdfreader.dsaconverter;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.dsaconverter.model.HerbRaw;
import de.pho.dsapdfreader.dsaconverter.model.TavernRaw;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsHerb;
import de.pho.dsapdfreader.dsaconverter.model.atomicflags.ConverterAtomicFlagsTavern;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DsaConverterTavern extends DsaConverter<TavernRaw, ConverterAtomicFlagsTavern>
{

  private static final String KEY_ALTERNATIVE_NAMEN = "";
  protected static final String[] KEYS = {
          KEY_ALTERNATIVE_NAMEN,
  };
  private ConverterAtomicFlagsTavern flags;

  @Override
  protected String[] getKeys()
  {
    return KEYS;
  }

  @Override
  protected ConverterAtomicFlagsTavern getFlags()
  {
    if (flags == null)
    {
      this.flags = new ConverterAtomicFlagsTavern();
    }
    return flags;
  }

  @Override
  public List<TavernRaw> convertTextWithMetaInfo(List<TextWithMetaInfo> texts, TopicConfiguration conf) {
    Map<String, TavernRaw> tavernMap = new HashMap<>();

    AtomicReference<String> currentName = new AtomicReference<>();
    texts.forEach(t -> {
      if(t.isBold && t.text.startsWith("• ")) {
        String name = t.text.replace("• ", "");
        currentName.set(name);
        tavernMap.put(currentName.get(), new TavernRaw());
        tavernMap.get(currentName.get()).name = name;
        tavernMap.get(currentName.get()).publication = conf.publication;
      } else if(t.isBold) {
        currentName.set(t.text);
      } else if(currentName.get() != null){
        System.out.println(currentName.get());
        tavernMap.get(currentName.get()).description = tavernMap.get(currentName.get()).description+ " " + t.text;
      }

    });
    return tavernMap.values().stream().toList();
  }


  @Override
  protected String getClassName()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  protected void handleFirstValue(List<TavernRaw> returnValue, TopicConfiguration conf, String cleanText)
  {

    if (!this.getFlags().getFirstFlag().get())
    {
      TavernRaw newEntry = new TavernRaw();
      this.getFlags().initDataFlags();
      newEntry.setTopic(conf.topic);
      newEntry.setPublication(conf.publication);
      returnValue.add(newEntry);
    }
    last(returnValue).setName(concatForDataValue(last(returnValue).getName(), cleanText));
  }

  @Override
  protected void applyFlagsForKey(String key)
  {
    this.getFlags().wasName.set(false);this.getFlags().wasName.set(false);
  }

  @Override
  protected void applyDataValue(TavernRaw currentDataObject, String cleanText, boolean isBold, boolean isItalic)
  {
    if (currentDataObject != null)
    {
      if (this.getFlags().wasName.get()) currentDataObject.name = concatForDataValueWithMarkup(currentDataObject.name, cleanText, isBold, isItalic);
    }
  }


  @Override
  protected boolean validateIsDataKey(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return t.size <= conf.dataSize && t.isBold && t.text != null && !t.text.isEmpty();
  }

  @Override
  protected boolean validateIsDataValue(TextWithMetaInfo t, String cleanText, TopicConfiguration conf) {
    return t.size <= conf.dataSize && !this.validateIsDataKey(t, cleanText, conf);
  }

  @Override
  protected void concludePredecessor(TavernRaw lastEntry) {
  }
}
