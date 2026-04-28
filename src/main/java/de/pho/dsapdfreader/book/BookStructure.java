package de.pho.dsapdfreader.book;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Datenmodell fuer den Strukturindex eines Buches (siehe
 * docs/book-structure-model.md). Wird von {@link BookInterpreter} erzeugt
 * und nach `_structure.json` serialisiert.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookStructure {

    public String publication;
    public String title;
    public Integer dsaVersion;
    public Integer pages;
    public String generatedAt;
    public String generatorVersion;

    public List<HierarchyNode> hierarchy = new ArrayList<>();
    public Map<String, PageEntry> pageIndex = new LinkedHashMap<>();
    public Assets assets = new Assets();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HierarchyNode {
        public String id;
        public int level;
        public String title;
        public int[] pageRange;
        public List<HierarchyNode> children = new ArrayList<>();
        public List<Block> blocks = new ArrayList<>();
        public List<String> boxes = new ArrayList<>();
        public List<String> images = new ArrayList<>();
        public List<String> tables = new ArrayList<>();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Block {
        public String id;
        /** ability | spell | liturgy | boon | talent | unclassified */
        public String kind;
        public String name;
        public Integer page;
        public Integer anchorOffset;
        public Map<String, String> fields = new LinkedHashMap<>();
        public List<String> boxes = new ArrayList<>();
        public List<String> images = new ArrayList<>();
        public List<String> tables = new ArrayList<>();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageEntry {
        public List<String> blocks = new ArrayList<>();
        public List<String> boxes = new ArrayList<>();
        public List<String> images = new ArrayList<>();
        public List<String> tables = new ArrayList<>();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Assets {
        public Map<String, ImageAsset> images = new LinkedHashMap<>();
        public Map<String, TableAsset> tables = new LinkedHashMap<>();
        public Map<String, BoxAsset> boxes = new LinkedHashMap<>();
    }

    public static class ImageAsset {
        public String path;
        public Integer page;
        public String anchorHierarchyId;
        public int[] sizePx;
        public String checksum;
    }

    public static class TableAsset {
        public String path;
        public Integer page;
        public String anchorHierarchyId;
        public String caption;
    }

    public static class BoxAsset {
        public String path;
        public Integer page;
        public String anchorHierarchyId;
        public String anchorBlockId;
        public String boxType;
    }
}
