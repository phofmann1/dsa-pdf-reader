package de.pho.realmworks.analyser;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import de.pho.realmworks.analyser.exportxsd.Alias;
import de.pho.realmworks.analyser.exportxsd.Category;
import de.pho.realmworks.analyser.exportxsd.CategoryGlobal;
import de.pho.realmworks.analyser.exportxsd.Connection;
import de.pho.realmworks.analyser.exportxsd.Domain;
import de.pho.realmworks.analyser.exportxsd.DomainGlobal;
import de.pho.realmworks.analyser.exportxsd.Export;
import de.pho.realmworks.analyser.exportxsd.Overlay;
import de.pho.realmworks.analyser.exportxsd.PartitionGlobal;
import de.pho.realmworks.analyser.exportxsd.Section;
import de.pho.realmworks.analyser.exportxsd.Tag;
import de.pho.realmworks.analyser.exportxsd.TagAssign;
import de.pho.realmworks.analyser.exportxsd.TagGlobal;
import de.pho.realmworks.analyser.exportxsd.TextOverride;
import de.pho.realmworks.analyser.exportxsd.Topic;

public class RealmWorksAnalyser
{
  static final String FILE_PATH = "C:\\develop\\project\\dsa-pdf-reader\\realmworks\\THINGS.rwexport";
  static final List<String> EIDS = List.of(
      "Domain_1",
      "Tag_2",
      "Tag_3",
      "Tag_4",
      "Tag_5",
      "Tag_6",
      "Tag_7",
      "Tag_8",
      "Tag_9",
      "Tag_10",
      "Tag_11",
      "Tag_12",
      "Tag_13",
      "Tag_14",
      "Tag_15",
      "Tag_16",
      "Tag_17",
      "Tag_18",
      "Tag_19",
      "Tag_20",
      "Tag_21",
      "Domain_2",
      "Tag_22",
      "Tag_23",
      "Tag_24",
      "Tag_25",
      "Tag_26",
      "Tag_27",
      "Tag_28",
      "Tag_29",
      "Tag_30",
      "Tag_31",
      "Tag_32",
      "Tag_33",
      "Tag_34",
      "Tag_35",
      "Tag_36",
      "Tag_37",
      "Tag_38",
      "Tag_39",
      "Tag_40",
      "Tag_41",
      "Domain_3",
      "Tag_42",
      "Tag_43",
      "Tag_44",
      "Tag_45",
      "Tag_46",
      "Tag_47",
      "Domain_4",
      "Tag_48",
      "Tag_49",
      "Tag_50",
      "Tag_51",
      "Tag_52",
      "Tag_53",
      "Tag_54",
      "Tag_55",
      "Tag_56",
      "Tag_57",
      "Tag_58",
      "Tag_59",
      "Domain_5",
      "Tag_60",
      "Tag_61",
      "Tag_62",
      "Tag_63",
      "Tag_64",
      "Tag_65",
      "Tag_66",
      "Tag_67",
      "Tag_68",
      "Tag_69",
      "Tag_70",
      "Tag_71",
      "Tag_72",
      "Tag_73",
      "Tag_74",
      "Tag_75",
      "Tag_76",
      "Tag_77",
      "Tag_78",
      "Tag_79",
      "Tag_80",
      "Tag_81",
      "Tag_82",
      "Tag_83",
      "Tag_84",
      "Tag_85",
      "Tag_86",
      "Tag_87",
      "Tag_88",
      "Tag_89",
      "Tag_90",
      "Tag_91",
      "Tag_92",
      "Tag_93",
      "Tag_94",
      "Tag_95",
      "Tag_96",
      "Tag_97",
      "Tag_98",
      "Tag_99",
      "Tag_100",
      "Tag_101",
      "Tag_102",
      "Tag_103",
      "Tag_104",
      "Tag_105",
      "Tag_106",
      "Tag_107",
      "Tag_108",
      "Tag_109",
      "Tag_110",
      "Tag_111",
      "Tag_112",
      "Tag_113",
      "Tag_114",
      "Tag_115",
      "Tag_116",
      "Domain_6",
      "Tag_117",
      "Tag_118",
      "Tag_119",
      "Tag_120",
      "Tag_121",
      "Tag_122",
      "Tag_123",
      "Tag_124",
      "Tag_125",
      "Tag_126",
      "Tag_127",
      "Tag_128",
      "Tag_129",
      "Tag_130",
      "Tag_131",
      "Tag_132",
      "Tag_133",
      "Tag_134",
      "Domain_7",
      "Tag_135",
      "Tag_136",
      "Tag_137",
      "Tag_138",
      "Tag_139",
      "Tag_140",
      "Tag_141",
      "Tag_142",
      "Tag_143",
      "Domain_8",
      "Tag_144",
      "Tag_145",
      "Tag_146",
      "Tag_147",
      "Tag_148",
      "Tag_149",
      "Tag_150",
      "Tag_151",
      "Tag_152",
      "Tag_153",
      "Tag_154",
      "Tag_155",
      "Tag_156",
      "Tag_157",
      "Tag_158",
      "Tag_159",
      "Tag_160",
      "Tag_161",
      "Domain_9",
      "Tag_162",
      "Tag_163",
      "Tag_164",
      "Tag_165",
      "Tag_166",
      "Tag_167",
      "Tag_168",
      "Tag_169",
      "Tag_170",
      "Tag_171",
      "Tag_172",
      "Tag_173",
      "Tag_174",
      "Tag_175",
      "Domain_10",
      "Tag_176",
      "Tag_177",
      "Tag_178",
      "Tag_179",
      "Tag_180",
      "Tag_181",
      "Tag_182",
      "Tag_183",
      "Tag_184",
      "Tag_185",
      "Tag_186",
      "Tag_187",
      "Tag_188",
      "Tag_189",
      "Tag_190",
      "Tag_191",
      "Tag_192",
      "Tag_193",
      "Tag_194",
      "Domain_11",
      "Tag_195",
      "Tag_196",
      "Tag_197",
      "Tag_198",
      "Tag_199",
      "Tag_200",
      "Tag_201",
      "Tag_202",
      "Tag_203",
      "Tag_204",
      "Tag_205",
      "Tag_206",
      "Tag_207",
      "Tag_208",
      "Tag_209",
      "Tag_210",
      "Tag_211",
      "Tag_212",
      "Tag_213",
      "Tag_214",
      "Tag_215",
      "Tag_216",
      "Tag_217",
      "Tag_218",
      "Tag_219",
      "Tag_220",
      "Tag_221",
      "Tag_222",
      "Tag_223",
      "Tag_224",
      "Tag_225",
      "Tag_226",
      "Tag_227",
      "Tag_228",
      "Tag_229",
      "Tag_230",
      "Tag_231",
      "Tag_232",
      "Tag_233",
      "Tag_234",
      "Tag_235",
      "Tag_236",
      "Tag_237",
      "Tag_238",
      "Tag_239",
      "Tag_240",
      "Tag_241",
      "Tag_242",
      "Tag_243",
      "Tag_244",
      "Tag_245",
      "Tag_246",
      "Tag_247",
      "Tag_248",
      "Tag_249",
      "Tag_250",
      "Domain_12",
      "Tag_251",
      "Tag_252",
      "Tag_253",
      "Tag_254",
      "Tag_255",
      "Tag_256",
      "Tag_257",
      "Tag_258",
      "Tag_259",
      "Tag_260",
      "Tag_261",
      "Tag_262",
      "Tag_263",
      "Tag_264",
      "Tag_265",
      "Tag_266",
      "Tag_267",
      "Tag_268",
      "Tag_269",
      "Tag_270",
      "Tag_271",
      "Tag_272",
      "Tag_273",
      "Tag_274",
      "Tag_275",
      "Tag_276",
      "Tag_277",
      "Tag_278",
      "Tag_279",
      "Tag_280",
      "Tag_281",
      "Tag_282",
      "Tag_283",
      "Tag_284",
      "Tag_285",
      "Tag_286",
      "Tag_287",
      "Tag_288",
      "Tag_289",
      "Tag_290",
      "Tag_291",
      "Tag_292",
      "Tag_293",
      "Tag_294",
      "Tag_295",
      "Tag_296",
      "Tag_297",
      "Tag_298",
      "Tag_299",
      "Tag_300",
      "Tag_301",
      "Tag_302",
      "Tag_303",
      "Tag_304",
      "Tag_305",
      "Tag_306",
      "Tag_307",
      "Tag_308",
      "Tag_309",
      "Tag_310",
      "Tag_311",
      "Tag_312",
      "Tag_313",
      "Tag_314",
      "Tag_315",
      "Tag_316",
      "Tag_317",
      "Tag_318",
      "Tag_319",
      "Tag_320",
      "Domain_13",
      "Tag_321",
      "Tag_322",
      "Tag_323",
      "Tag_324",
      "Tag_325",
      "Tag_326",
      "Tag_327",
      "Tag_328",
      "Tag_329",
      "Tag_330",
      "Tag_331",
      "Tag_332",
      "Tag_333",
      "Tag_334",
      "Tag_335",
      "Tag_336",
      "Tag_337",
      "Tag_338",
      "Tag_339",
      "Tag_340",
      "Tag_341",
      "Tag_342",
      "Tag_343",
      "Tag_344",
      "Tag_345",
      "Tag_346",
      "Tag_347",
      "Tag_348",
      "Tag_349",
      "Tag_350",
      "Tag_351",
      "Tag_352",
      "Tag_353",
      "Tag_354",
      "Tag_355",
      "Tag_356",
      "Tag_357",
      "Tag_358",
      "Tag_359",
      "Tag_360",
      "Tag_361",
      "Tag_362",
      "Tag_363",
      "Tag_364",
      "Tag_365",
      "Tag_366",
      "Tag_367",
      "Tag_368",
      "Tag_369",
      "Tag_370",
      "Tag_371",
      "Tag_372",
      "Tag_373",
      "Tag_374",
      "Tag_375",
      "Tag_376",
      "Tag_377",
      "Tag_378",
      "Tag_379",
      "Tag_380",
      "Tag_381",
      "Tag_382",
      "Tag_383",
      "Tag_384",
      "Tag_385",
      "Tag_386",
      "Tag_387",
      "Tag_388",
      "Tag_389",
      "Tag_390",
      "Tag_391",
      "Tag_392",
      "Tag_393",
      "Tag_394",
      "Tag_395",
      "Tag_396",
      "Tag_397",
      "Tag_398",
      "Tag_399",
      "Tag_400",
      "Tag_401",
      "Tag_402",
      "Domain_14",
      "Tag_403",
      "Tag_404",
      "Tag_405",
      "Tag_406",
      "Tag_407",
      "Tag_408",
      "Tag_409",
      "Tag_410",
      "Tag_411",
      "Tag_412",
      "Tag_413",
      "Tag_414",
      "Tag_415",
      "Tag_416",
      "Tag_417",
      "Tag_418",
      "Tag_419",
      "Tag_420",
      "Tag_421",
      "Tag_422",
      "Tag_423",
      "Tag_424",
      "Tag_425",
      "Tag_426",
      "Tag_427",
      "Tag_428",
      "Tag_429",
      "Tag_430",
      "Tag_431",
      "Tag_432",
      "Tag_433",
      "Tag_434",
      "Tag_435",
      "Tag_436",
      "Tag_437",
      "Tag_438",
      "Tag_439",
      "Domain_15",
      "Tag_440",
      "Tag_441",
      "Tag_442",
      "Tag_443",
      "Tag_444",
      "Tag_445",
      "Tag_446",
      "Tag_447",
      "Tag_448",
      "Tag_449",
      "Tag_450",
      "Tag_451",
      "Domain_16",
      "Tag_452",
      "Tag_453",
      "Tag_454",
      "Tag_455",
      "Tag_456",
      "Tag_457",
      "Tag_458",
      "Tag_459",
      "Tag_460",
      "Tag_461",
      "Tag_462",
      "Tag_463",
      "Tag_464",
      "Tag_465",
      "Tag_466",
      "Tag_467",
      "Tag_468",
      "Tag_469",
      "Tag_470",
      "Tag_471",
      "Tag_472",
      "Tag_473",
      "Tag_474",
      "Tag_475",
      "Tag_476",
      "Tag_477",
      "Tag_478",
      "Tag_479",
      "Tag_480",
      "Tag_481",
      "Tag_482",
      "Tag_483",
      "Tag_484",
      "Tag_485",
      "Tag_486",
      "Tag_487",
      "Domain_17",
      "Tag_488",
      "Tag_489",
      "Tag_490",
      "Tag_491",
      "Tag_492",
      "Tag_493",
      "Tag_494",
      "Domain_18",
      "Tag_495",
      "Tag_496",
      "Tag_497",
      "Tag_498",
      "Tag_499",
      "Tag_500",
      "Tag_501",
      "Tag_502",
      "Tag_503",
      "Tag_504",
      "Tag_505",
      "Tag_506",
      "Tag_507",
      "Tag_508",
      "Domain_19",
      "Tag_509",
      "Tag_510",
      "Tag_511",
      "Tag_512",
      "Tag_513",
      "Domain_20",
      "Tag_514",
      "Tag_515",
      "Tag_516",
      "Tag_517",
      "Tag_518",
      "Tag_519",
      "Tag_520",
      "Tag_521",
      "Tag_522",
      "Tag_523",
      "Tag_524",
      "Tag_525",
      "Tag_526",
      "Tag_527",
      "Tag_528",
      "Tag_529",
      "Tag_530",
      "Domain_21",
      "Tag_531",
      "Tag_532",
      "Tag_533",
      "Tag_534",
      "Tag_535",
      "Tag_536",
      "Tag_537",
      "Tag_538",
      "Tag_539",
      "Tag_540",
      "Tag_541",
      "Tag_542",
      "Tag_543",
      "Tag_544",
      "Tag_545",
      "Tag_546",
      "Tag_547",
      "Tag_548",
      "Domain_22",
      "Tag_549",
      "Tag_550",
      "Tag_551",
      "Tag_552",
      "Tag_553",
      "Tag_554",
      "Tag_555",
      "Tag_556",
      "Tag_557",
      "Tag_558",
      "Tag_559",
      "Tag_560",
      "Tag_561",
      "Tag_562",
      "Tag_563",
      "Tag_564",
      "Tag_565",
      "Tag_566",
      "Tag_567",
      "Tag_568",
      "Tag_569",
      "Tag_570",
      "Tag_571",
      "Tag_572",
      "Tag_573",
      "Tag_574",
      "Tag_575",
      "Tag_576",
      "Tag_577",
      "Tag_578",
      "Tag_579",
      "Tag_580",
      "Tag_581",
      "Tag_582",
      "Tag_583",
      "Tag_584",
      "Tag_585",
      "Tag_586",
      "Tag_587",
      "Tag_588",
      "Tag_589",
      "Tag_590",
      "Tag_591",
      "Tag_592",
      "Tag_593",
      "Tag_594",
      "Tag_595",
      "Tag_596",
      "Tag_597",
      "Tag_598",
      "Tag_599",
      "Tag_600",
      "Tag_601",
      "Tag_602",
      "Tag_603",
      "Tag_604",
      "Tag_605",
      "Tag_606",
      "Tag_607",
      "Tag_608",
      "Tag_609",
      "Tag_610",
      "Tag_611",
      "Domain_23",
      "Tag_612",
      "Tag_613",
      "Tag_614",
      "Tag_615",
      "Tag_616",
      "Tag_617",
      "Tag_618",
      "Tag_619",
      "Tag_620",
      "Tag_621",
      "Tag_622",
      "Tag_623",
      "Domain_24",
      "Tag_624",
      "Tag_625",
      "Tag_626",
      "Tag_627",
      "Tag_628",
      "Tag_629",
      "Tag_630",
      "Tag_631",
      "Tag_632",
      "Tag_633",
      "Tag_634",
      "Tag_635",
      "Tag_636",
      "Tag_637",
      "Tag_638",
      "Tag_639",
      "Tag_640",
      "Tag_641",
      "Tag_642",
      "Domain_25",
      "Tag_643",
      "Tag_644",
      "Tag_645",
      "Tag_646",
      "Tag_647",
      "Tag_648",
      "Tag_649",
      "Tag_650",
      "Tag_651",
      "Tag_652",
      "Tag_653",
      "Tag_654",
      "Tag_655",
      "Tag_656",
      "Tag_657",
      "Tag_658",
      "Tag_659",
      "Tag_660",
      "Tag_661",
      "Domain_26",
      "Tag_662",
      "Tag_663",
      "Tag_664",
      "Tag_665",
      "Tag_666",
      "Tag_667",
      "Tag_668",
      "Tag_669",
      "Tag_670",
      "Tag_671",
      "Tag_672",
      "Tag_673",
      "Tag_674",
      "Tag_675",
      "Tag_676",
      "Tag_677",
      "Tag_678",
      "Tag_679",
      "Tag_680",
      "Tag_681",
      "Tag_682",
      "Tag_683",
      "Tag_684",
      "Tag_685",
      "Tag_686",
      "Tag_687",
      "Tag_688",
      "Tag_689",
      "Tag_690",
      "Tag_691",
      "Tag_692",
      "Tag_693",
      "Domain_27",
      "Tag_694",
      "Tag_695",
      "Tag_696",
      "Tag_697",
      "Tag_698",
      "Tag_699",
      "Tag_700",
      "Tag_701",
      "Tag_702",
      "Tag_703",
      "Tag_704",
      "Tag_705",
      "Tag_706",
      "Tag_707",
      "Tag_708",
      "Tag_709",
      "Tag_710",
      "Tag_711",
      "Tag_712",
      "Tag_713",
      "Tag_714",
      "Tag_715",
      "Tag_716",
      "Tag_717",
      "Tag_718",
      "Tag_719",
      "Tag_720",
      "Tag_721",
      "Tag_722",
      "Tag_723",
      "Tag_724",
      "Tag_725",
      "Tag_726",
      "Tag_727",
      "Domain_28",
      "Tag_728",
      "Tag_729",
      "Tag_730",
      "Tag_731",
      "Tag_732",
      "Tag_733",
      "Tag_734",
      "Tag_735",
      "Tag_736",
      "Domain_29",
      "Tag_737",
      "Tag_738",
      "Tag_739",
      "Tag_740",
      "Tag_741",
      "Tag_742",
      "Tag_743",
      "Tag_744",
      "Tag_745",
      "Tag_746",
      "Tag_747",
      "Domain_30",
      "Tag_748",
      "Tag_749",
      "Tag_750",
      "Tag_751",
      "Tag_752",
      "Tag_753",
      "Tag_754",
      "Tag_755",
      "Tag_756",
      "Tag_757",
      "Tag_758",
      "Tag_759",
      "Tag_760",
      "Tag_761",
      "Tag_762",
      "Tag_763",
      "Tag_764",
      "Tag_765",
      "Tag_766",
      "Tag_767",
      "Tag_768",
      "Tag_769",
      "Tag_770",
      "Tag_771",
      "Tag_772",
      "Tag_773",
      "Tag_774",
      "Tag_775",
      "Tag_776",
      "Tag_777",
      "Tag_778",
      "Tag_779",
      "Tag_780",
      "Tag_781",
      "Tag_782",
      "Tag_783",
      "Tag_784",
      "Tag_785",
      "Tag_786",
      "Tag_787",
      "Domain_31",
      "Tag_788",
      "Tag_789",
      "Tag_790",
      "Tag_791",
      "Tag_792",
      "Tag_793",
      "Tag_794",
      "Tag_795",
      "Tag_796",
      "Tag_797",
      "Tag_798",
      "Tag_799",
      "Tag_800",
      "Tag_801",
      "Tag_802",
      "Tag_803",
      "Tag_804",
      "Tag_805",
      "Tag_806",
      "Domain_32",
      "Tag_807",
      "Tag_808",
      "Tag_809",
      "Tag_810",
      "Tag_811",
      "Tag_812",
      "Tag_813",
      "Tag_814",
      "Tag_815",
      "Tag_816",
      "Tag_817",
      "Tag_818",
      "Tag_819",
      "Tag_820",
      "Tag_821",
      "Tag_822",
      "Tag_823",
      "Domain_33",
      "Tag_824",
      "Tag_825",
      "Tag_826",
      "Tag_827",
      "Tag_828",
      "Tag_829",
      "Tag_830",
      "Tag_831",
      "Tag_832",
      "Tag_833",
      "Tag_834",
      "Tag_835",
      "Tag_836",
      "Tag_837",
      "Tag_838",
      "Tag_839",
      "Tag_840",
      "Tag_841",
      "Tag_842",
      "Tag_843",
      "Tag_844",
      "Tag_845",
      "Tag_846",
      "Tag_847",
      "Tag_848",
      "Tag_849",
      "Domain_34",
      "Tag_850",
      "Tag_851",
      "Tag_852",
      "Tag_853",
      "Tag_854",
      "Tag_855",
      "Tag_856",
      "Tag_857",
      "Tag_858",
      "Tag_859",
      "Tag_860",
      "Domain_35",
      "Tag_861",
      "Tag_862",
      "Tag_863",
      "Tag_864",
      "Tag_865",
      "Tag_866",
      "Tag_867",
      "Tag_868",
      "Tag_869",
      "Tag_870",
      "Tag_871",
      "Tag_872",
      "Tag_873",
      "Tag_874",
      "Tag_875",
      "Tag_876",
      "Tag_877",
      "Tag_878",
      "Tag_879",
      "Tag_880",
      "Domain_36",
      "Tag_881",
      "Tag_882",
      "Tag_883",
      "Tag_884",
      "Tag_885",
      "Tag_886",
      "Tag_887",
      "Tag_888",
      "Tag_889",
      "Tag_890",
      "Tag_891",
      "Tag_892",
      "Tag_893",
      "Tag_894",
      "Tag_895",
      "Tag_896",
      "Tag_897",
      "Tag_898",
      "Domain_37",
      "Tag_899",
      "Tag_900",
      "Tag_901",
      "Tag_902",
      "Tag_903",
      "Tag_904",
      "Tag_905",
      "Tag_906",
      "Tag_907",
      "Tag_908",
      "Tag_909",
      "Tag_910",
      "Tag_911",
      "Tag_912",
      "Tag_913",
      "Domain_38",
      "Tag_914",
      "Tag_915",
      "Tag_916",
      "Tag_917",
      "Tag_918",
      "Tag_919",
      "Tag_920",
      "Tag_921",
      "Tag_922",
      "Tag_923",
      "Tag_924",
      "Tag_925",
      "Tag_926",
      "Domain_39",
      "Tag_927",
      "Tag_928",
      "Tag_929",
      "Tag_930",
      "Tag_931",
      "Tag_932",
      "Domain_40",
      "Tag_933",
      "Tag_934",
      "Tag_935",
      "Tag_936",
      "Tag_937",
      "Tag_938",
      "Domain_41",
      "Tag_939",
      "Tag_940",
      "Tag_941",
      "Tag_942",
      "Tag_943",
      "Tag_944",
      "Tag_945",
      "Tag_946",
      "Tag_947",
      "Tag_948",
      "Domain_42",
      "Tag_949",
      "Tag_950",
      "Tag_951",
      "Tag_952",
      "Tag_953",
      "Tag_954",
      "Tag_955",
      "Tag_956",
      "Tag_957",
      "Tag_958",
      "Tag_959",
      "Tag_960",
      "Tag_961",
      "Tag_962",
      "Tag_963",
      "Tag_964",
      "Tag_965",
      "Tag_966",
      "Tag_967",
      "Tag_968",
      "Tag_969",
      "Tag_970",
      "Tag_971",
      "Tag_972",
      "Tag_973",
      "Tag_974",
      "Tag_975",
      "Tag_976",
      "Tag_977",
      "Tag_978",
      "Tag_979",
      "Tag_980",
      "Tag_981",
      "Tag_982",
      "Tag_983",
      "Domain_43",
      "Tag_984",
      "Tag_985",
      "Tag_986",
      "Tag_987",
      "Tag_988",
      "Tag_989",
      "Tag_990",
      "Tag_991",
      "Tag_992",
      "Tag_993",
      "Tag_994",
      "Tag_995",
      "Tag_996",
      "Tag_997",
      "Tag_998",
      "Tag_999",
      "Tag_1000",
      "Tag_1001",
      "Tag_1002",
      "Tag_1003",
      "Tag_1004",
      "Domain_44",
      "Tag_1005",
      "Tag_1006",
      "Tag_1007",
      "Tag_1008",
      "Tag_1009",
      "Tag_1010",
      "Tag_1011",
      "Tag_1012",
      "Tag_1013",
      "Tag_1014",
      "Tag_1015",
      "Tag_1016",
      "Domain_45",
      "Tag_1017",
      "Tag_1018",
      "Tag_1019",
      "Tag_1020",
      "Tag_1021",
      "Tag_1022",
      "Tag_1023",
      "Tag_1024",
      "Tag_1025",
      "Tag_1026",
      "Tag_1027",
      "Tag_1028",
      "Tag_1029",
      "Tag_1030",
      "Tag_1031",
      "Tag_1032",
      "Tag_1033",
      "Tag_1034",
      "Tag_1035",
      "Tag_1036",
      "Tag_1037",
      "Tag_1038",
      "Tag_1039",
      "Tag_1040",
      "Tag_1041",
      "Tag_1042",
      "Tag_1043",
      "Tag_1044",
      "Tag_1045",
      "Tag_1046",
      "Tag_1047",
      "Tag_1048",
      "Tag_1049",
      "Tag_1050",
      "Tag_1051",
      "Tag_1052",
      "Tag_1053",
      "Tag_1054",
      "Tag_1055",
      "Tag_1056",
      "Tag_1057",
      "Tag_1058",
      "Tag_1059",
      "Tag_1060",
      "Tag_1061",
      "Tag_1062",
      "Tag_1063",
      "Tag_1064",
      "Tag_1065",
      "Tag_1066",
      "Tag_1067",
      "Tag_1068",
      "Tag_1069",
      "Tag_1070",
      "Tag_1071",
      "Tag_1072",
      "Tag_1073",
      "Tag_1074",
      "Tag_1075",
      "Tag_1076",
      "Tag_1077",
      "Tag_1078",
      "Tag_1079",
      "Tag_1080",
      "Domain_46",
      "Tag_1081",
      "Tag_1082",
      "Tag_1083",
      "Tag_1084",
      "Tag_1085",
      "Tag_1086",
      "Tag_1087",
      "Tag_1088",
      "Tag_1089",
      "Tag_1090",
      "Tag_1091",
      "Tag_1092",
      "Tag_1093",
      "Tag_1094",
      "Tag_1095",
      "Tag_1096",
      "Tag_1097",
      "Tag_1098",
      "Tag_1099",
      "Tag_1100",
      "Tag_1101",
      "Tag_1102",
      "Tag_1103",
      "Domain_47",
      "Tag_1104",
      "Tag_1105",
      "Tag_1106",
      "Tag_1107",
      "Tag_1108",
      "Tag_1109",
      "Tag_1110",
      "Tag_1111",
      "Tag_1112",
      "Tag_1113",
      "Tag_1114",
      "Tag_1115",
      "Tag_1116",
      "Tag_1117",
      "Tag_1118",
      "Tag_1119",
      "Tag_1120",
      "Tag_1121",
      "Tag_1122",
      "Domain_48",
      "Tag_1123",
      "Tag_1124",
      "Tag_1125",
      "Tag_1126",
      "Tag_1127",
      "Tag_1128",
      "Tag_1129",
      "Tag_1130",
      "Tag_1131",
      "Tag_1132",
      "Tag_1133",
      "Tag_1134",
      "Tag_1135",
      "Tag_1136",
      "Tag_1137",
      "Tag_1138",
      "Tag_1139",
      "Tag_1140",
      "Domain_49",
      "Tag_1141",
      "Tag_1142",
      "Domain_50",
      "Tag_1143",
      "Tag_1144",
      "Tag_1145",
      "Tag_1146",
      "Tag_1147",
      "Tag_1148",
      "Tag_1149",
      "Tag_1150",
      "Tag_1151",
      "Tag_1152",
      "Tag_1153",
      "Tag_1154",
      "Tag_1155",
      "Tag_1156",
      "Tag_1157",
      "Tag_1158",
      "Tag_1159",
      "Tag_1160",
      "Tag_1161",
      "Tag_1162",
      "Tag_1163",
      "Tag_1164",
      "Tag_1165",
      "Tag_1166",
      "Tag_1167",
      "Tag_1168",
      "Tag_1169",
      "Tag_1170",
      "Tag_1171",
      "Tag_1172",
      "Tag_1173",
      "Tag_1174",
      "Tag_1175",
      "Tag_1176",
      "Tag_1177",
      "Tag_1178",
      "Domain_51",
      "Tag_1179",
      "Tag_1180",
      "Tag_1181",
      "Tag_1182",
      "Tag_1183",
      "Tag_1184",
      "Tag_1185",
      "Tag_1186",
      "Tag_1187",
      "Tag_1188",
      "Tag_1189",
      "Tag_1190",
      "Tag_1191",
      "Tag_1192",
      "Tag_1193",
      "Tag_1194",
      "Tag_1195",
      "Tag_1196",
      "Domain_52",
      "Tag_1197",
      "Tag_1198",
      "Tag_1199",
      "Tag_1200",
      "Tag_1201",
      "Tag_1202",
      "Tag_1203",
      "Tag_1204",
      "Tag_1205",
      "Tag_1206",
      "Tag_1207",
      "Tag_1208",
      "Tag_1209",
      "Tag_1210",
      "Tag_1211",
      "Tag_1212",
      "Tag_1213",
      "Tag_1214",
      "Tag_1215",
      "Tag_1216",
      "Domain_53",
      "Tag_1217",
      "Tag_1218",
      "Tag_1219",
      "Tag_1220",
      "Tag_1221",
      "Tag_1222",
      "Tag_1223",
      "Tag_1224",
      "Tag_1225",
      "Tag_1226",
      "Tag_1227",
      "Tag_1228",
      "Tag_1229",
      "Tag_1230",
      "Tag_1231",
      "Tag_1232",
      "Tag_1233",
      "Tag_1234",
      "Tag_1235",
      "Tag_1236",
      "Tag_1237",
      "Tag_1238",
      "Tag_1239",
      "Tag_1240",
      "Tag_1241",
      "Tag_1242",
      "Tag_1243",
      "Tag_1244",
      "Tag_1245",
      "Tag_1246",
      "Tag_1247",
      "Tag_1248",
      "Tag_1249",
      "Tag_1250",
      "Tag_1251",
      "Tag_1252",
      "Tag_1253",
      "Tag_1254",
      "Domain_54",
      "Tag_1255",
      "Tag_1256",
      "Tag_1257",
      "Tag_1258",
      "Tag_1259",
      "Tag_1260",
      "Tag_1261",
      "Tag_1262",
      "Tag_1263",
      "Tag_1264",
      "Tag_1265",
      "Tag_1266",
      "Domain_55",
      "Tag_1267",
      "Tag_1268",
      "Tag_1269",
      "Tag_1270",
      "Tag_1271",
      "Tag_1272",
      "Tag_1273",
      "Tag_1274",
      "Tag_1275",
      "Tag_1276",
      "Tag_1277",
      "Tag_1278",
      "Tag_1279",
      "Tag_1280",
      "Tag_1281",
      "Tag_1282",
      "Tag_1283",
      "Domain_56",
      "Tag_1284",
      "Tag_1285",
      "Tag_1286",
      "Tag_1287",
      "Tag_1288",
      "Tag_1289",
      "Tag_1290",
      "Tag_1291",
      "Tag_1292",
      "Tag_1293",
      "Tag_1294",
      "Tag_1295",
      "Tag_1296",
      "Tag_1297",
      "Tag_1298",
      "Tag_1299",
      "Tag_1300",
      "Tag_1301",
      "Tag_1302",
      "Tag_1303",
      "Tag_1304",
      "Domain_57",
      "Tag_1305",
      "Tag_1306",
      "Tag_1307",
      "Tag_1308",
      "Domain_58",
      "Tag_1309",
      "Tag_1310",
      "Tag_1311",
      "Tag_1312",
      "Domain_59",
      "Tag_1313",
      "Tag_1314",
      "Tag_1315",
      "Tag_1316",
      "Domain_60",
      "Tag_1317",
      "Tag_1318",
      "Tag_1319",
      "Tag_1320",
      "Tag_1321",
      "Tag_1322",
      "Tag_1323",
      "Tag_1324",
      "Tag_1325",
      "Domain_61",
      "Tag_1326",
      "Tag_1327",
      "Tag_1328",
      "Tag_1329",
      "Tag_1330",
      "Tag_1331",
      "Tag_1332",
      "Tag_1333",
      "Tag_1334",
      "Tag_1335",
      "Domain_62",
      "Tag_1336",
      "Tag_1337",
      "Tag_1338",
      "Tag_1339",
      "Tag_1340",
      "Tag_1341",
      "Tag_1342",
      "Tag_1343",
      "Domain_63",
      "Tag_1344",
      "Tag_1345",
      "Tag_1346",
      "Tag_1347",
      "Tag_1348",
      "Tag_1349",
      "Tag_1350",
      "Tag_1351",
      "Tag_1352",
      "Tag_1353",
      "Tag_1354",
      "Tag_1355",
      "Domain_64",
      "Tag_1356",
      "Tag_1357",
      "Tag_1358",
      "Tag_1359",
      "Tag_1360",
      "Tag_1361",
      "Tag_1362",
      "Tag_1363",
      "Tag_1364",
      "Tag_1365",
      "Domain_65",
      "Tag_1366",
      "Tag_1367",
      "Tag_1368",
      "Tag_1369",
      "Tag_1370",
      "Tag_1371",
      "Domain_66",
      "Tag_1372",
      "Tag_1373",
      "Tag_1374",
      "Tag_1375",
      "Domain_67",
      "Tag_1376",
      "Tag_1377",
      "Tag_1378",
      "Tag_1379",
      "Domain_68",
      "Tag_1380",
      "Tag_1381",
      "Tag_1382",
      "Tag_1383",
      "Domain_69",
      "Tag_1",
      "Category_1",
      "Category_2",
      "Category_3",
      "Category_4",
      "Category_5",
      "Category_6",
      "Category_7",
      "Category_8",
      "Category_9",
      "Category_10",
      "Category_11",
      "Category_12",
      "Category_13",
      "Category_14",
      "Category_15",
      "Category_16",
      "Category_17",
      "Category_18",
      "Category_19",
      "Category_20",
      "Category_21",
      "Category_22",
      "Category_23",
      "Category_24",
      "Category_25",
      "Category_26",
      "Category_27",
      "Category_28",
      "Category_29",
      "Category_30",
      "Category_31",
      "Category_32",
      "Category_33",
      "Category_34",
      "Category_35",
      "Category_36",
      "Category_37",
      "Category_38",
      "Category_39",
      "Category_40",
      "Category_41",
      "Category_42",
      "Category_43",
      "Category_44",
      "Category_45",
      "Category_46",
      "Category_47",
      "Category_48",
      "Category_49",
      "Category_50",
      "Category_51",
      "Category_52",
      "Category_53",
      "Category_54",
      "Category_55",
      "Category_56",
      "Category_57",
      "Category_58",
      "Category_59",
      "Category_60",
      "Category_61",
      "Category_62",
      "Category_63",
      "Category_64",
      "Category_65",
      "Category_66",
      "Category_67",
      "Category_68",
      "Category_69",
      "Category_70",
      "Category_71",
      "Category_72",
      "Category_73",
      "Category_74",
      "Category_75",
      "Category_76",
      "Category_77",
      "Category_78",
      "Category_79",
      "Category_80",
      "Category_81",
      "Category_82",
      "Category_83",
      "Category_84",
      "Category_85",
      "Category_86",
      "Category_87",
      "Category_88",
      "Category_89",
      "Category_90",
      "Category_91",
      "Category_92",
      "Category_93",
      "Category_94",
      "Category_95",
      "Category_96",
      "Category_97",
      "Category_98",
      "Category_99",
      "Category_100",
      "Category_101",
      "Category_102",
      "Category_103",
      "Category_104",
      "Category_105",
      "Category_106",
      "Category_107",
      "Category_108",
      "Category_109",
      "Category_110",
      "Category_111",
      "Category_112",
      "Category_113",
      "Category_114",
      "Category_115",
      "Category_116",
      "Category_117",
      "Category_118",
      "Category_119",
      "Category_120",
      "Category_121",
      "Category_122",
      "Category_123",
      "Category_124",
      "Category_125",
      "Category_126",
      "Category_127",
      "Category_128",
      "Category_129",
      "Category_130",
      "Category_131",
      "Category_132",
      "Category_133"
  );

  public static void main(String[] args)
  {
    try
    {
      JAXBContext jaxbContext = JAXBContext.newInstance(Export.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      Export rootObject = (Export) unmarshaller.unmarshal(new File(FILE_PATH));

      List<String> existingIds = EIDS;

      rootObject.getStructure().getContent().forEach(obj -> {
        System.out.print(obj+"-> ");
        if(obj instanceof DomainGlobal) handleStructureDomainGlobal((DomainGlobal) obj, existingIds);
        else if(obj instanceof Domain) handleStructureDomain((Domain) obj, existingIds);
        else if(obj instanceof Category) handleStructureCategory((Category) obj, existingIds);
        else if(obj instanceof CategoryGlobal) handleStructureCategoryGlobal((CategoryGlobal) obj, existingIds);
        else {
          System.err.println("ERROR STRUCTURE: " + obj.getClass());
        }
      });

      rootObject.getContents().getContent().forEach(cnt -> {
        if (cnt instanceof Topic) handleContentTopic((Topic) cnt, existingIds);
        else if (cnt instanceof String)
        {
        }
        else
        {
          System.err.println("ERROR CONTENT: " + cnt.getClass());
        }
      });

    }
    catch (JAXBException e)
    {
      e.printStackTrace();
    }

  }

  private static void handleContentTopic(Topic topic, List<String> existingIds)
  {
    if (!existingIds.contains(topic.getCategoryId()))
      System.err.println("CATEGORY-ID MISSING (" + topic.getCategoryId() + "): " + topic.getPublicName());

    topic.getContent().forEach(cnt -> {
      if (cnt instanceof Topic) handleContentTopic((Topic) cnt, existingIds);
      else if (cnt instanceof Section)
      {
      }
      else if (cnt instanceof Alias)
      {
      }
      else if (cnt instanceof TagAssign) handleContentTagAssign((TagAssign) cnt, existingIds);
      else if (cnt instanceof Connection) handleContentConnection((Connection) cnt, existingIds);
      else System.out.println("ERROR CONTENT in (" + topic.getPublicName() + ") : " + cnt.getClass());
    });
  }

  private static void handleContentConnection(Connection connection, List<String> existingIds)
  {

  }

  private static void handleContentTagAssign(TagAssign cnt, List<String> existingIds)
  {
    if (!existingIds.contains(cnt.getTagId())) System.out.println("ERROR CONTENT TAG: " + cnt.getTagId());
  }

  private static void handleStructureCategory(Category category, List<String> existingIds)
  {
    existingIds.add(category.getCategoryId());
    System.out.println(category.getCategoryId());
    category.getPartition().forEach(partition -> {
      existingIds.add(partition.getPartitionId());
    });
  }

  private static void handleStructureCategoryGlobal(CategoryGlobal cg, List<String> existingIds)
  {
    System.out.println(cg.getCategoryId());
    existingIds.add(cg.getCategoryId());
    cg.getContent().forEach(o -> handleStructureContent(o, existingIds));
  }

  private static void handleStructureContent(Object o, List<String> existingIds)
  {
    if (o instanceof PartitionGlobal)
    {
      existingIds.add(((PartitionGlobal) o).getPartitionId());
    }
    else if (o instanceof CategoryGlobal) handleStructureCategoryGlobal((CategoryGlobal) o, existingIds);
    else if (o instanceof Category) handleStructureCategory((Category) o, existingIds);
    else if (o instanceof TextOverride)
    {
    }
    else
    {
      System.err.println("ERROR handleStructureContent:" + o.getClass());
    }
  }

  private static void handleStructureDomain(Domain d, List<String> existingIds)
  {
    System.out.println(d.getDomainId());
    existingIds.add(d.getDomainId());
    d.getTag().forEach(t -> existingIds.add(t.getTagId()));
  }

  private static void handleStructureDomainGlobal(DomainGlobal dg, List<String> existingIds)
  {
    System.out.println(dg.getDomainId());
    existingIds.add(dg.getDomainId());
    dg.getContent().forEach(t -> {
      if (t instanceof Tag)
        existingIds.add(((Tag) t).getTagId());
      else if (t instanceof TagGlobal)
      {
        existingIds.add(((TagGlobal) t).getTagId());
      }
      else if (t instanceof TextOverride)
      {
      }
      else if (t instanceof Overlay)
      {
      }
      else System.err.println("ERROR TAG: " + t.getClass());
    });
  }


}
