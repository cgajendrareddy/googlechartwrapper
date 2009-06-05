package de.toolforge.googlechartwrapper;

import googlechartwrapper.color.ChartColor;
import googlechartwrapper.color.IChartColorable;
import googlechartwrapper.color.ISolidFillable;
import googlechartwrapper.color.SolidFill;
import googlechartwrapper.color.SolidFill.ChartFillDestination;
import googlechartwrapper.util.GenericAppender;

import java.awt.Dimension;
import java.util.Collections;
import java.util.List;

import de.toolforge.googlechartwrapper.coder.EncoderFactory;
import de.toolforge.googlechartwrapper.coder.EncodingType;
import de.toolforge.googlechartwrapper.coder.IEncoder;

/*
 * enums from <a href="http://code.google.com/p/charts4j/source/browse/trunk/src/com/googlecode/charts4j/USAState.java"
 * > here</a>
 */
/**
 * Specifies a map <a href="http://code.google.com/apis/chart/types.html#maps">
 * http://code.google.com/apis/chart/types.html#maps</a>.
 * 
 * <p>
 * Here are some examples of how map can be used:
 * <p><blockquote><pre>
 *     List<Country> member = new LinkedList<Country>();
 *     member.add(new Map.Country(CountryName.GERMANY,100));
 *     
 *     Map eu = new Map(new Dimension(400,200),GeographicalArea.EUROPE,member);
 * </pre></blockquote>
 * <p>
 * 
 * @author martin
 * @author steffan
 * @version 03/17/09 
 * @see Country
 * @see CountryCode
 * @see CountryName
 * @see State
 * @see StateCode
 * @see StateName
 * @see GeographicalArea
 * 
 */
public class Map extends AbstractChart implements ISolidFillable,
		IChartColorable {

	private GeographicalArea area;
	protected GenericAppender<SolidFill> solidFillAppender = new GenericAppender<SolidFill>(
			ChartTypeFeature.SolidFill);
	protected GenericAppender<ChartColor> chartColorAppender = new GenericAppender<ChartColor>(
			ChartTypeFeature.ChartColor, ",");

	private List<State> stateList;
	private List<Country> countryList;

	/**
	 * All possible areas.
	 * 
	 * @author steffan
	 *
	 */
	public enum GeographicalArea {
		AFRICA, ASIA, EUROPE, MIDDLE_EAST, SOUTH_AMERICA, USA, WORLD;

		/**
		 * Returns the name as lower case string.
		 * 
		 * @return lower case string
		 */
		public String getAreaCode() {
			return this.toString().toLowerCase();
		}
	}

	/**
	 * Constructs a new map, with usa as {@link Country}, the list contains all states.
	 * 
	 * @param chartDimension
	 * @param stateList
	 *            list of states {@link State}, can be empty to show only the
	 *            us.
	 * 
	 * @throws IllegalArgumentException
	 *             if stateList is {@code null} or member is {@code null}
	 * @throws IllegalArgumentException
	 *             if chartDimension is out of range
	 */
	public Map(Dimension chartDimension, List<State> stateList) {
		super(chartDimension);

		if (stateList == null)
			throw new IllegalArgumentException("stateList can not be null");

		List<State> copy = Collections.unmodifiableList(stateList);
		for (State current : copy) {
			if (current == null)
				throw new IllegalArgumentException("listMember can not be null");
		}
		if (chartDimension.height > 220) {
			throw new IllegalArgumentException("height must be <= 220");
		}
		if (chartDimension.width > 440) {
			throw new IllegalArgumentException("width must be <= 440");
		}

		this.stateList = copy;
		this.area = GeographicalArea.USA;
	}

	/**
	 * Constructs a new map, all areas are possible. If only the us is wanted choose {@link Map#Map(Dimension, List)}.
	 * 
	 * @param chartDimension
	 * @param area
	 *            the area to show {@link GeographicalArea}
	 * @param countryList
	 *            a list of {@link Country}, can be empty, it is also possible
	 *            to select USA , but if states in the us are necessary use
	 *            {@link Map#Map(Dimension, List)}.
	 * 
	 * @throws IllegalArgumentException
	 *             if stateList is {@code null} or member is {@code null}
	 * @throws IllegalArgumentException
	 *             if chartDimension is out of range
	 * @throws IllegalArgumentException
	 *             if area is {@code null}
	 */
	public Map(Dimension chartDimension, GeographicalArea area,
			List<Country> countryList) {
		super(chartDimension);

		if (area == null)
			throw new IllegalArgumentException("area can not be null");
		if (countryList == null)
			throw new IllegalArgumentException("countryList can not be null");
		List<Country> copy = Collections.unmodifiableList(countryList);
		for (Country current : copy) {
			if (current == null)
				throw new IllegalArgumentException("listMember can not be null");
		}
		if (chartDimension.height > 220) {
			throw new IllegalArgumentException("height must be <= 220");
		}
		if (chartDimension.width > 440) {
			throw new IllegalArgumentException("width must be <= 440");
		}

		this.countryList = copy;
		this.area = area;
	}

	@Override
	protected ChartType getChartType() {
		return ChartType.Map;
	}

	@Override
	protected String getUrlChartType() {
		return ChartType.Map.getPrefix();
	}

	@Override
	protected void collectUrlElements() {
		super.collectUrlElements();

		urlElements.offer("chtm=" + area.getAreaCode()); // area code

		// the usa case
		if (this.stateList != null) {
			StringBuilder states = new StringBuilder();
			StringBuilder colorLevel = new StringBuilder();

			// see what's in the list
			for (State current : stateList) {
				states.append(current.getCode());
				colorLevel.append(current.getColorLevel());
				colorLevel.append(",");
			}
			// cut the last ','
			if (colorLevel.length() > 1) {
				colorLevel.deleteCharAt(colorLevel.length() - 1);
			}

			urlElements.offer("chld=" + states.toString());
			urlElements.offer("chd=t:" + colorLevel);
		}
		// the world case

		if (this.countryList != null) {
			StringBuilder countries = new StringBuilder();
			StringBuilder colorLevel = new StringBuilder();

			// see whats in the list
			for (Country current : countryList) {
				countries.append(current.getCode());
				colorLevel.append(current.getColorLevel());
				colorLevel.append(",");
			}
			// cut the last ','
			if (colorLevel.length() > 1) {
				colorLevel.deleteCharAt(colorLevel.length() - 1);
			}

			urlElements.offer("chld=" + countries.toString());
			urlElements.offer("chd=t:" + colorLevel);
		}
	}

	/**
	 * Only supports {@link ChartFillDestination#Background}.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@link SolidFill#getChartFillDestination()} does not
	 *             equals ChartFillDestination#Background
	 */
	public void addSolidFill(SolidFill sf) {
		if (!sf.getChartFillDestination().equals(
				ChartFillDestination.Background)) {
			throw new IllegalArgumentException(
					"only ChartFillDestination.Background supported");
		}
		solidFillAppender.add(sf);
	}

	public List<SolidFill> getSolidFills() {
		return solidFillAppender.getList();
	}

	public void removeAllSolidFills() {
		solidFillAppender.removeAll();

	}

	public SolidFill removeSolidFill(int index) {
		return solidFillAppender.remove(index);
	}

	public boolean removeSolidFill(SolidFill sf) {
		return solidFillAppender.remove(sf);
	}

	public IEncoder getEncoder() {

		// we use always t: , so give them the textEncoder
		return EncoderFactory.getEncoder(EncodingType.TextEncoding);
	}

	/**
	 * {@inheritDoc}
	 * </br />
	 * <b>Note</b>
	 * <br />
	 * The first color in list is the default color, the rest will be used to interpolate, remember the color level in the
	 * {@link State} and {@link Country} constructors.
	 */
	public void addChartColor(ChartColor cc) {

		this.chartColorAppender.add(cc);
	}

	public List<ChartColor> getChartColors() {

		return this.chartColorAppender.getList().size() > 0 ? this.chartColorAppender
				.getList()
				: null;
	}

	public void removeAllChartColors() {
		this.chartColorAppender.removeAll();

	}

	public ChartColor removeChartColor(int index) {

		return this.chartColorAppender.remove(index);
	}

	public boolean removeChartColor(ChartColor cc) {

		return this.chartColorAppender.remove(cc);
	}

	/**
	 * Country names.
	 */
	public enum CountryName {
		/** Afghanistan. **/
		AFGHANISTAN("AF"),
		/** Aland_islands. **/
		ALAND_ISLANDS("AX"),
		/** Albania. **/
		ALBANIA("AL"),
		/** Algeria. **/
		ALGERIA("DZ"),
		/** American_samoa. **/
		AMERICAN_SAMOA("AS"),
		/** Andorra. **/
		ANDORRA("AD"),
		/** Angola. **/
		ANGOLA("AO"),
		/** Anguilla. **/
		ANGUILLA("AI"),
		/** Antarctica. **/
		ANTARCTICA("AQ"),
		/** Antigua_and_barbuda. **/
		ANTIGUA_AND_BARBUDA("AG"),
		/** Argentina. **/
		ARGENTINA("AR"),
		/** Armenia. **/
		ARMENIA("AM"),
		/** Aruba. **/
		ARUBA("AW"),
		/** Australia. **/
		AUSTRALIA("AU"),
		/** Austria. **/
		AUSTRIA("AT"),
		/** Azerbaijan. **/
		AZERBAIJAN("AZ"),
		/** Bahamas. **/
		BAHAMAS("BS"),
		/** Bahrain. **/
		BAHRAIN("BH"),
		/** Bangladesh. **/
		BANGLADESH("BD"),
		/** Barbados. **/
		BARBADOS("BB"),
		/** Belarus. **/
		BELARUS("BY"),
		/** Belgium. **/
		BELGIUM("BE"),
		/** Belize. **/
		BELIZE("BZ"),
		/** Benin. **/
		BENIN("BJ"),
		/** Bermuda. **/
		BERMUDA("BM"),
		/** Bhutan. **/
		BHUTAN("BT"),
		/** Bolivia. **/
		BOLIVIA("BO"),
		/** Bosnia_and_herzegovina. **/
		BOSNIA_AND_HERZEGOVINA("BA"),
		/** Botswana. **/
		BOTSWANA("BW"),
		/** Bouvet_island. **/
		BOUVET_ISLAND("BV"),
		/** Brazil. **/
		BRAZIL("BR"),
		/** British_indian_ocean_territory. **/
		BRITISH_INDIAN_OCEAN_TERRITORY("IO"),
		/** Brunei_darussalam. **/
		BRUNEI_DARUSSALAM("BN"),
		/** Bulgaria. **/
		BULGARIA("BG"),
		/** Burkina_faso. **/
		BURKINA_FASO("BF"),
		/** Burundi. **/
		BURUNDI("BI"),
		/** Cambodia. **/
		CAMBODIA("KH"),
		/** Cameroon. **/
		CAMEROON("CM"),
		/** Canada. **/
		CANADA("CA"),
		/** Cape_verde. **/
		CAPE_VERDE("CV"),
		/** Cayman_islands. **/
		CAYMAN_ISLANDS("KY"),
		/** Central_african_republic. **/
		CENTRAL_AFRICAN_REPUBLIC("CF"),
		/** Chad. **/
		CHAD("TD"),
		/** Chile. **/
		CHILE("CL"),
		/** China. **/
		CHINA("CN"),
		/** Christmas_island. **/
		CHRISTMAS_ISLAND("CX"),
		/** Cocos_keeling_islands. **/
		COCOS_KEELING_ISLANDS("CC"),
		/** Colombia. **/
		COLOMBIA("CO"),
		/** Comoros. **/
		COMOROS("KM"),
		/** Congo. **/
		CONGO("CG"),
		/** Congo_the_democratic_republic_of_the. **/
		CONGO_THE_DEMOCRATIC_REPUBLIC_OF_THE("CD"),
		/** Cook_islands. **/
		COOK_ISLANDS("CK"),
		/** Costa_rica. **/
		COSTA_RICA("CR"),
		/** Cote_d_ivoire. **/
		COTE_D_IVOIRE("CI"),
		/** Croatia. **/
		CROATIA("HR"),
		/** Cuba. **/
		CUBA("CU"),
		/** Cyprus. **/
		CYPRUS("CY"),
		/** Czech_republic. **/
		CZECH_REPUBLIC("CZ"),
		/** Denmark. **/
		DENMARK("DK"),
		/** Djibouti. **/
		DJIBOUTI("DJ"),
		/** Dominica. **/
		DOMINICA("DM"),
		/** Dominican_republic. **/
		DOMINICAN_REPUBLIC("DO"),
		/** Ecuador. **/
		ECUADOR("EC"),
		/** Egypt. **/
		EGYPT("EG"),
		/** El_salvador. **/
		EL_SALVADOR("SV"),
		/** Equatorial_guinea. **/
		EQUATORIAL_GUINEA("GQ"),
		/** Eritrea. **/
		ERITREA("ER"),
		/** Estonia. **/
		ESTONIA("EE"),
		/** Ethiopia. **/
		ETHIOPIA("ET"),
		/** Falkland_islands_malvinas_. **/
		FALKLAND_ISLANDS_MALVINAS_("FK"),
		/** Faroe_islands. **/
		FAROE_ISLANDS("FO"),
		/** Fiji. **/
		FIJI("FJ"),
		/** Finland. **/
		FINLAND("FI"),
		/** France. **/
		FRANCE("FR"),
		/** French_guiana. **/
		FRENCH_GUIANA("GF"),
		/** French_polynesia. **/
		FRENCH_POLYNESIA("PF"),
		/** French_southern_territories. **/
		FRENCH_SOUTHERN_TERRITORIES("TF"),
		/** Gabon. **/
		GABON("GA"),
		/** Gambia. **/
		GAMBIA("GM"),
		/** Georgia. **/
		GEORGIA("GE"),
		/** Germany. **/
		GERMANY("DE"),
		/** Ghana. **/
		GHANA("GH"),
		/** Gibraltar. **/
		GIBRALTAR("GI"),
		/** Greece. **/
		GREECE("GR"),
		/** Greenland. **/
		GREENLAND("GL"),
		/** Grenada. **/
		GRENADA("GD"),
		/** Guadeloupe. **/
		GUADELOUPE("GP"),
		/** Guam. **/
		GUAM("GU"),
		/** Guatemala. **/
		GUATEMALA("GT"),
		/** Guernsey. **/
		GUERNSEY("GG"),
		/** Guinea. **/
		GUINEA("GN"),
		/** Guinea_bissau. **/
		GUINEA_BISSAU("GW"),
		/** Guyana. **/
		GUYANA("GY"),
		/** Haiti. **/
		HAITI("HT"),
		/** Heard_island_and_mcdonald_islands. **/
		HEARD_ISLAND_AND_MCDONALD_ISLANDS("HM"),
		/** Holy_see_vatican_city_state_. **/
		HOLY_SEE_VATICAN_CITY_STATE_("VA"),
		/** Honduras. **/
		HONDURAS("HN"),
		/** Hong_kong. **/
		HONG_KONG("HK"),
		/** Hungary. **/
		HUNGARY("HU"),
		/** Iceland. **/
		ICELAND("IS"),
		/** India. **/
		INDIA("IN"),
		/** Indonesia. **/
		INDONESIA("ID"),
		/** Iran_islamic_republic_of. **/
		IRAN_ISLAMIC_REPUBLIC_OF("IR"),
		/** Iraq. **/
		IRAQ("IQ"),
		/** Ireland. **/
		IRELAND("IE"),
		/** Isle_of_man. **/
		ISLE_OF_MAN("IM"),
		/** Israel. **/
		ISRAEL("IL"),
		/** Italy. **/
		ITALY("IT"),
		/** Jamaica. **/
		JAMAICA("JM"),
		/** Japan. **/
		JAPAN("JP"),
		/** Jersey. **/
		JERSEY("JE"),
		/** Jordan. **/
		JORDAN("JO"),
		/** Kazakhstan. **/
		KAZAKHSTAN("KZ"),
		/** Kenya. **/
		KENYA("KE"),
		/** Kiribati. **/
		KIRIBATI("KI"),
		/** Korea_democratic_peoples_republic_of. **/
		KOREA_DEMOCRATIC_PEOPLES_REPUBLIC_OF("KP"),
		/** Korea_republic_of. **/
		KOREA_REPUBLIC_OF("KR"),
		/** Kuwait. **/
		KUWAIT("KW"),
		/** Kyrgyzstan. **/
		KYRGYZSTAN("KG"),
		/** Lao_peoples_democratic_republic. **/
		LAO_PEOPLES_DEMOCRATIC_REPUBLIC("LA"),
		/** Latvia. **/
		LATVIA("LV"),
		/** Lebanon. **/
		LEBANON("LB"),
		/** Lesotho. **/
		LESOTHO("LS"),
		/** Liberia. **/
		LIBERIA("LR"),
		/** Libyan_arab_jamahiriya. **/
		LIBYAN_ARAB_JAMAHIRIYA("LY"),
		/** Liechtenstein. **/
		LIECHTENSTEIN("LI"),
		/** Lithuania. **/
		LITHUANIA("LT"),
		/** Luxembourg. **/
		LUXEMBOURG("LU"),
		/** Macao. **/
		MACAO("MO"),
		/** Macedonia_the_former_yugoslav_republic_of. **/
		MACEDONIA_THE_FORMER_YUGOSLAV_REPUBLIC_OF("MK"),
		/** Madagascar. **/
		MADAGASCAR("MG"),
		/** Malawi. **/
		MALAWI("MW"),
		/** Malaysia. **/
		MALAYSIA("MY"),
		/** Maldives. **/
		MALDIVES("MV"),
		/** Mali. **/
		MALI("ML"),
		/** Malta. **/
		MALTA("MT"),
		/** Marshall_islands. **/
		MARSHALL_ISLANDS("MH"),
		/** Martinique. **/
		MARTINIQUE("MQ"),
		/** Mauritania. **/
		MAURITANIA("MR"),
		/** Mauritius. **/
		MAURITIUS("MU"),
		/** Mayotte. **/
		MAYOTTE("YT"),
		/** Mexico. **/
		MEXICO("MX"),
		/** Micronesia_federated_states_of. **/
		MICRONESIA_FEDERATED_STATES_OF("FM"),
		/** Moldova_republic_of. **/
		MOLDOVA_REPUBLIC_OF("MD"),
		/** Monaco. **/
		MONACO("MC"),
		/** Mongolia. **/
		MONGOLIA("MN"),
		/** Montenegro. **/
		MONTENEGRO("ME"),
		/** Montserrat. **/
		MONTSERRAT("MS"),
		/** Morocco. **/
		MOROCCO("MA"),
		/** Mozambique. **/
		MOZAMBIQUE("MZ"),
		/** Myanmar. **/
		MYANMAR("MM"),
		/** Namibia. **/
		NAMIBIA("NA"),
		/** Nauru. **/
		NAURU("NR"),
		/** Nepal. **/
		NEPAL("NP"),
		/** Netherlands. **/
		NETHERLANDS("NL"),
		/** Netherlands_antilles. **/
		NETHERLANDS_ANTILLES("AN"),
		/** New_caledonia. **/
		NEW_CALEDONIA("NC"),
		/** New_zealand. **/
		NEW_ZEALAND("NZ"),
		/** Nicaragua. **/
		NICARAGUA("NI"),
		/** Niger. **/
		NIGER("NE"),
		/** Nigeria. **/
		NIGERIA("NG"),
		/** Niue. **/
		NIUE("NU"),
		/** Norfolk_island. **/
		NORFOLK_ISLAND("NF"),
		/** Northern_mariana_islands. **/
		NORTHERN_MARIANA_ISLANDS("MP"),
		/** Norway. **/
		NORWAY("NO"),
		/** Oman. **/
		OMAN("OM"),
		/** Pakistan. **/
		PAKISTAN("PK"),
		/** Palau. **/
		PALAU("PW"),
		/** Palestinian_territory_occupied. **/
		PALESTINIAN_TERRITORY_OCCUPIED("PS"),
		/** Panama. **/
		PANAMA("PA"),
		/** Papua_new_guinea. **/
		PAPUA_NEW_GUINEA("PG"),
		/** Paraguay. **/
		PARAGUAY("PY"),
		/** Peru. **/
		PERU("PE"),
		/** Philippines. **/
		PHILIPPINES("PH"),
		/** Pitcairn. **/
		PITCAIRN("PN"),
		/** Poland. **/
		POLAND("PL"),
		/** Portugal. **/
		PORTUGAL("PT"),
		/** Puerto_rico. **/
		PUERTO_RICO("PR"),
		/** Qatar. **/
		QATAR("QA"),
		/** Reunion. **/
		REUNION("RE"),
		/** Romania. **/
		ROMANIA("RO"),
		/** Russian_federation. **/
		RUSSIAN_FEDERATION("RU"),
		/** Rwanda. **/
		RWANDA("RW"),
		/** Saint_barthelemy. **/
		SAINT_BARTHELEMY("BL"),
		/** Saint_helena. **/
		SAINT_HELENA("SH"),
		/** Saint_kitts_and_nevis. **/
		SAINT_KITTS_AND_NEVIS("KN"),
		/** Saint_lucia. **/
		SAINT_LUCIA("LC"),
		/** Saint_martin. **/
		SAINT_MARTIN("MF"),
		/** Saint_pierre_and_miquelon. **/
		SAINT_PIERRE_AND_MIQUELON("PM"),
		/** Saint_vincent_and_the_grenadines. **/
		SAINT_VINCENT_AND_THE_GRENADINES("VC"),
		/** Samoa. **/
		SAMOA("WS"),
		/** San_marino. **/
		SAN_MARINO("SM"),
		/** Sao_tome_and_principe. **/
		SAO_TOME_AND_PRINCIPE("ST"),
		/** Saudi_arabia. **/
		SAUDI_ARABIA("SA"),
		/** Senegal. **/
		SENEGAL("SN"),
		/** Serbia. **/
		SERBIA("RS"),
		/** Seychelles. **/
		SEYCHELLES("SC"),
		/** Sierra_leone. **/
		SIERRA_LEONE("SL"),
		/** Singapore. **/
		SINGAPORE("SG"),
		/** Slovakia. **/
		SLOVAKIA("SK"),
		/** Slovenia. **/
		SLOVENIA("SI"),
		/** Solomon_islands. **/
		SOLOMON_ISLANDS("SB"),
		/** Somalia. **/
		SOMALIA("SO"),
		/** South_africa. **/
		SOUTH_AFRICA("ZA"),
		/** South_georgia_and_the_south_sandwich_islands. **/
		SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS("GS"),
		/** Spain. **/
		SPAIN("ES"),
		/** Sri_lanka. **/
		SRI_LANKA("LK"),
		/** Sudan. **/
		SUDAN("SD"),
		/** Suriname. **/
		SURINAME("SR"),
		/** Svalbard_and_jan_mayen. **/
		SVALBARD_AND_JAN_MAYEN("SJ"),
		/** Swaziland. **/
		SWAZILAND("SZ"),
		/** Sweden. **/
		SWEDEN("SE"),
		/** Switzerland. **/
		SWITZERLAND("CH"),
		/** Syrian_arab_republic. **/
		SYRIAN_ARAB_REPUBLIC("SY"),
		/** Taiwan_province_of_china. **/
		TAIWAN_PROVINCE_OF_CHINA("TW"),
		/** Tajikistan. **/
		TAJIKISTAN("TJ"),
		/** Tanzania_united_republic_of. **/
		TANZANIA_UNITED_REPUBLIC_OF("TZ"),
		/** Thailand. **/
		THAILAND("TH"),
		/** Timor_leste. **/
		TIMOR_LESTE("TL"),
		/** Togo. **/
		TOGO("TG"),
		/** Tokelau. **/
		TOKELAU("TK"),
		/** Tonga. **/
		TONGA("TO"),
		/** Trinidad_and_tobago. **/
		TRINIDAD_AND_TOBAGO("TT"),
		/** Tunisia. **/
		TUNISIA("TN"),
		/** Turkey. **/
		TURKEY("TR"),
		/** Turkmenistan. **/
		TURKMENISTAN("TM"),
		/** Turks_and_caicos_islands. **/
		TURKS_AND_CAICOS_ISLANDS("TC"),
		/** Tuvalu. **/
		TUVALU("TV"),
		/** Uganda. **/
		UGANDA("UG"),
		/** Ukraine. **/
		UKRAINE("UA"),
		/** United_arab_emirates. **/
		UNITED_ARAB_EMIRATES("AE"),
		/** United_kingdom. **/
		UNITED_KINGDOM("GB"),
		/** United_states. **/
		UNITED_STATES("US"),
		/** United_states_minor_outlying_islands. **/
		UNITED_STATES_MINOR_OUTLYING_ISLANDS("UM"),
		/** Uruguay. **/
		URUGUAY("UY"),
		/** Uzbekistan. **/
		UZBEKISTAN("UZ"),
		/** Vanuatu. **/
		VANUATU("VU"),
		/** Venezuela. **/
		VENEZUELA("VE"),
		/** Viet_nam. **/
		VIET_NAM("VN"),
		/** Virgin_islands_british. **/
		VIRGIN_ISLANDS_BRITISH("VG"),
		/** Virgin_islands_us. **/
		VIRGIN_ISLANDS_US("VI"),
		/** Wallis_and_futuna. **/
		WALLIS_AND_FUTUNA("WF"),
		/** Western_sahara. **/
		WESTERN_SAHARA("EH"),
		/** Yemen. **/
		YEMEN("YE"),
		/** Zambia. **/
		ZAMBIA("ZM"),
		/** Zimbabwe. **/
		ZIMBABWE("ZW");

		/** String for the Google Chart API parameter. */
		private final String name;

		/**
		 * Instantiating enum.
		 * 
		 * @param name
		 *            the string for the Google Chart API parameter
		 */
		private CountryName(final String name) {
			this.name = name;
		}
		/**
		 * Returns the country code as string, e.g. ZW for Zimbabwe.
		 * 
		 * @return the country code
		 */
		public String getCountryCode() {
			return this.name;
		}
	}

	/**
	 * Country code.
	 * 
	 */
	public enum CountryCode {
		/** Afghanistan. **/
		AF,
		/** Aland_islands. **/
		AX,
		/** Albania. **/
		AL,
		/** Algeria. **/
		DZ,
		/** American_samoa. **/
		AS,
		/** Andorra. **/
		AD,
		/** Angola. **/
		AO,
		/** Anguilla. **/
		AI,
		/** Antarctica. **/
		AQ,
		/** Antigua_and_barbuda. **/
		AG,
		/** Argentina. **/
		AR,
		/** Armenia. **/
		AM,
		/** Aruba. **/
		AW,
		/** Australia. **/
		AU,
		/** Austria. **/
		AT,
		/** Azerbaijan. **/
		AZ,
		/** Bahamas. **/
		BS,
		/** Bahrain. **/
		BH,
		/** Bangladesh. **/
		BD,
		/** Barbados. **/
		BB,
		/** Belarus. **/
		BY,
		/** Belgium. **/
		BE,
		/** Belize. **/
		BZ,
		/** Benin. **/
		BJ,
		/** Bermuda. **/
		BM,
		/** Bhutan. **/
		BT,
		/** Bolivia. **/
		BO,
		/** Bosnia_and_herzegovina. **/
		BA,
		/** Botswana. **/
		BW,
		/** Bouvet_island. **/
		BV,
		/** Brazil. **/
		BR,
		/** British_indian_ocean_territory. **/
		IO,
		/** Brunei_darussalam. **/
		BN,
		/** Bulgaria. **/
		BG,
		/** Burkina_faso. **/
		BF,
		/** Burundi. **/
		BI,
		/** Cambodia. **/
		KH,
		/** Cameroon. **/
		CM,
		/** Canada. **/
		CA,
		/** Cape_verde. **/
		CV,
		/** Cayman_islands. **/
		KY,
		/** Central_african_republic. **/
		CF,
		/** Chad. **/
		TD,
		/** Chile. **/
		CL,
		/** China. **/
		CN,
		/** Christmas_island. **/
		CX,
		/** Cocos_keeling_islands. **/
		CC,
		/** Colombia. **/
		CO,
		/** Comoros. **/
		KM,
		/** Congo. **/
		CG,
		/** Congo_the_democratic_republic_of_the. **/
		CD,
		/** Cook_islands. **/
		CK,
		/** Costa_rica. **/
		CR,
		/** Cote_d_ivoire. **/
		CI,
		/** Croatia. **/
		HR,
		/** Cuba. **/
		CU,
		/** Cyprus. **/
		CY,
		/** Czech_republic. **/
		CZ,
		/** Denmark. **/
		DK,
		/** Djibouti. **/
		DJ,
		/** Dominica. **/
		DM,
		/** Dominican_republic. **/
		DO,
		/** Ecuador. **/
		EC,
		/** Egypt. **/
		EG,
		/** El_salvador. **/
		SV,
		/** Equatorial_guinea. **/
		GQ,
		/** Eritrea. **/
		ER,
		/** Estonia. **/
		EE,
		/** Ethiopia. **/
		ET,
		/** Falkland_islands_malvinas_. **/
		FK,
		/** Faroe_islands. **/
		FO,
		/** Fiji. **/
		FJ,
		/** Finland. **/
		FI,
		/** France. **/
		FR,
		/** French_guiana. **/
		GF,
		/** French_polynesia. **/
		PF,
		/** French_southern_territories. **/
		TF,
		/** Gabon. **/
		GA,
		/** Gambia. **/
		GM,
		/** Georgia. **/
		GE,
		/** Germany. **/
		DE,
		/** Ghana. **/
		GH,
		/** Gibraltar. **/
		GI,
		/** Greece. **/
		GR,
		/** Greenland. **/
		GL,
		/** Grenada. **/
		GD,
		/** Guadeloupe. **/
		GP,
		/** Guam. **/
		GU,
		/** Guatemala. **/
		GT,
		/** Guernsey. **/
		GG,
		/** Guinea. **/
		GN,
		/** Guinea_bissau. **/
		GW,
		/** Guyana. **/
		GY,
		/** Haiti. **/
		HT,
		/** Heard_island_and_mcdonald_islands. **/
		HM,
		/** Holy_see_vatican_city_state_. **/
		VA,
		/** Honduras. **/
		HN,
		/** Hong_kong. **/
		HK,
		/** Hungary. **/
		HU,
		/** Iceland. **/
		IS,
		/** India. **/
		IN,
		/** Indonesia. **/
		ID,
		/** Iran_islamic_republic_of. **/
		IR,
		/** Iraq. **/
		IQ,
		/** Ireland. **/
		IE,
		/** Isle_of_man. **/
		IM,
		/** Israel. **/
		IL,
		/** Italy. **/
		IT,
		/** Jamaica. **/
		JM,
		/** Japan. **/
		JP,
		/** Jersey. **/
		JE,
		/** Jordan. **/
		JO,
		/** Kazakhstan. **/
		KZ,
		/** Kenya. **/
		KE,
		/** Kiribati. **/
		KI,
		/** Korea_democratic_peoples_republic_of. **/
		KP,
		/** Korea_republic_of. **/
		KR,
		/** Kuwait. **/
		KW,
		/** Kyrgyzstan. **/
		KG,
		/** Lao_peoples_democratic_republic. **/
		LA,
		/** Latvia. **/
		LV,
		/** Lebanon. **/
		LB,
		/** Lesotho. **/
		LS,
		/** Liberia. **/
		LR,
		/** Libyan_arab_jamahiriya. **/
		LY,
		/** Liechtenstein. **/
		LI,
		/** Lithuania. **/
		LT,
		/** Luxembourg. **/
		LU,
		/** Macao. **/
		MO,
		/** Macedonia_the_former_yugoslav_republic_of. **/
		MK,
		/** Madagascar. **/
		MG,
		/** Malawi. **/
		MW,
		/** Malaysia. **/
		MY,
		/** Maldives. **/
		MV,
		/** Mali. **/
		ML,
		/** Malta. **/
		MT,
		/** Marshall_islands. **/
		MH,
		/** Martinique. **/
		MQ,
		/** Mauritania. **/
		MR,
		/** Mauritius. **/
		MU,
		/** Mayotte. **/
		YT,
		/** Mexico. **/
		MX,
		/** Micronesia_federated_states_of. **/
		FM,
		/** Moldova_republic_of. **/
		MD,
		/** Monaco. **/
		MC,
		/** Mongolia. **/
		MN,
		/** Montenegro. **/
		ME,
		/** Montserrat. **/
		MS,
		/** Morocco. **/
		MA,
		/** Mozambique. **/
		MZ,
		/** Myanmar. **/
		MM,
		/** Namibia. **/
		NA,
		/** Nauru. **/
		NR,
		/** Nepal. **/
		NP,
		/** Netherlands. **/
		NL,
		/** Netherlands_antilles. **/
		AN,
		/** New_caledonia. **/
		NC,
		/** New_zealand. **/
		NZ,
		/** Nicaragua. **/
		NI,
		/** Niger. **/
		NE,
		/** Nigeria. **/
		NG,
		/** Niue. **/
		NU,
		/** Norfolk_island. **/
		NF,
		/** Northern_mariana_islands. **/
		MP,
		/** Norway. **/
		NO,
		/** Oman. **/
		OM,
		/** Pakistan. **/
		PK,
		/** Palau. **/
		PW,
		/** Palestinian_territory_occupied. **/
		PS,
		/** Panama. **/
		PA,
		/** Papua_new_guinea. **/
		PG,
		/** Paraguay. **/
		PY,
		/** Peru. **/
		PE,
		/** Philippines. **/
		PH,
		/** Pitcairn. **/
		PN,
		/** Poland. **/
		PL,
		/** Portugal. **/
		PT,
		/** Puerto_rico. **/
		PR,
		/** Qatar. **/
		QA,
		/** Reunion. **/
		RE,
		/** Romania. **/
		RO,
		/** Russian_federation. **/
		RU,
		/** Rwanda. **/
		RW,
		/** Saint_barthelemy. **/
		BL,
		/** Saint_helena. **/
		SH,
		/** Saint_kitts_and_nevis. **/
		KN,
		/** Saint_lucia. **/
		LC,
		/** Saint_martin. **/
		MF,
		/** Saint_pierre_and_miquelon. **/
		PM,
		/** Saint_vincent_and_the_grenadines. **/
		VC,
		/** Samoa. **/
		WS,
		/** San_marino. **/
		SM,
		/** Sao_tome_and_principe. **/
		ST,
		/** Saudi_arabia. **/
		SA,
		/** Senegal. **/
		SN,
		/** Serbia. **/
		RS,
		/** Seychelles. **/
		SC,
		/** Sierra_leone. **/
		SL,
		/** Singapore. **/
		SG,
		/** Slovakia. **/
		SK,
		/** Slovenia. **/
		SI,
		/** Solomon_islands. **/
		SB,
		/** Somalia. **/
		SO,
		/** South_africa. **/
		ZA,
		/** South_georgia_and_the_south_sandwich_islands. **/
		GS,
		/** Spain. **/
		ES,
		/** Sri_lanka. **/
		LK,
		/** Sudan. **/
		SD,
		/** Suriname. **/
		SR,
		/** Svalbard_and_jan_mayen. **/
		SJ,
		/** Swaziland. **/
		SZ,
		/** Sweden. **/
		SE,
		/** Switzerland. **/
		CH,
		/** Syrian_arab_republic. **/
		SY,
		/** Taiwan_province_of_china. **/
		TW,
		/** Tajikistan. **/
		TJ,
		/** Tanzania_united_republic_of. **/
		TZ,
		/** Thailand. **/
		TH,
		/** Timor_leste. **/
		TL,
		/** Togo. **/
		TG,
		/** Tokelau. **/
		TK,
		/** Tonga. **/
		TO,
		/** Trinidad_and_tobago. **/
		TT,
		/** Tunisia. **/
		TN,
		/** Turkey. **/
		TR,
		/** Turkmenistan. **/
		TM,
		/** Turks_and_caicos_islands. **/
		TC,
		/** Tuvalu. **/
		TV,
		/** Uganda. **/
		UG,
		/** Ukraine. **/
		UA,
		/** United_arab_emirates. **/
		AE,
		/** United_kingdom. **/
		GB,
		/** United_states. **/
		US,
		/** United_states_minor_outlying_islands. **/
		UM,
		/** Uruguay. **/
		UY,
		/** Uzbekistan. **/
		UZ,
		/** Vanuatu. **/
		VU,
		/** Venezuela. **/
		VE,
		/** Viet_nam. **/
		VN,
		/** Virgin_islands_british. **/
		VG,
		/** Virgin_islands_us. **/
		VI,
		/** Wallis_and_futuna. **/
		WF,
		/** Western_sahara. **/
		EH,
		/** Yemen. **/
		YE,
		/** Zambia. **/
		ZM,
		/** Zimbabwe. **/
		ZW;
	}

	/**
	 * USA State name.
	 */
	public enum StateName {
		/** Alabama. **/
		ALABAMA("AL"),
		/** Alaska. **/
		ALASKA("AK"),
		/** Arizona. **/
		ARIZONA("AZ"),
		/** Arkansas. **/
		ARKANSAS("AR"),
		/** California. **/
		CALIFORNIA("CA"),
		/** Colorado. **/
		COLORADO("CO"),
		/** Connecticut. **/
		CONNECTICUT("CT"),
		/** Delaware. **/
		DELAWARE("DE"),
		/** Florida. **/
		FLORIDA("FL"),
		/** Georgia. **/
		GEORGIA("GA"),
		/** Hawaii. **/
		HAWAII("HI"),
		/** Idaho. **/
		IDAHO("ID"),
		/** Illinois. **/
		ILLINOIS("IL"),
		/** Indiana. **/
		INDIANA("IN"),
		/** Iowa. **/
		IOWA("IA"),
		/** Kansas. **/
		KANSAS("KS"),
		/** Kentucky. **/
		KENTUCKY("KY"),
		/** Louisiana. **/
		LOUISIANA("LA"),
		/** Maine. **/
		MAINE("ME"),
		/** Maryland. **/
		MARYLAND("MD"),
		/** Massachusetts. **/
		MASSACHUSETTS("MA"),
		/** Michigan. **/
		MICHIGAN("MI"),
		/** Minnesota. **/
		MINNESOTA("MN"),
		/** Mississippi. **/
		MISSISSIPPI("MS"),
		/** Missouri. **/
		MISSOURI("MO"),
		/** Montana. **/
		MONTANA("MT"),
		/** Nebraska. **/
		NEBRASKA("NE"),
		/** Nevada. **/
		NEVADA("NV"),
		/** New_hampshire. **/
		NEW_HAMPSHIRE("NH"),
		/** New_jersey. **/
		NEW_JERSEY("NJ"),
		/** New_mexico. **/
		NEW_MEXICO("NM"),
		/** New_york. **/
		NEW_YORK("NY"),
		/** North_carolina. **/
		NORTH_CAROLINA("NC"),
		/** North_dakota. **/
		NORTH_DAKOTA("ND"),
		/** Ohio. **/
		OHIO("OH"),
		/** Oklahoma. **/
		OKLAHOMA("OK"),
		/** Oregon. **/
		OREGON("OR"),
		/** Pennsylvania. **/
		PENNSYLVANIA("PA"),
		/** Rhode_island. **/
		RHODE_ISLAND("RI"),
		/** South_carolina. **/
		SOUTH_CAROLINA("SC"),
		/** South_dakota. **/
		SOUTH_DAKOTA("SD"),
		/** Tennessee. **/
		TENNESSEE("TN"),
		/** Texas. **/
		TEXAS("TX"),
		/** Utah. **/
		UTAH("UT"),
		/** Vermont. **/
		VERMONT("VT"),
		/** Virginia. **/
		VIRGINIA("VA"),
		/** Washington. **/
		WASHINGTON("WA"),
		/** West_virginia. **/
		WEST_VIRGINIA("WV"),
		/** Wisconsin. **/
		WISCONSIN("WI"),
		/** Wyoming. **/
		WYOMING("WY");

		/** String for the Google Chart API parameter. */
		private final String name;

		/**
		 * Instantiating enum.
		 * 
		 * @param name
		 *            the string for the Google Chart API parameter
		 */
		private StateName(final String name) {
			this.name = name;
		}

		/**
		 * Returns the state code, e.g. WY for Wyoming.
		 * 
		 * @return the country code
		 */
		public String getStateCode() {
			return this.name;
		}
	}

	/**
	 * USA state name code.
	 * 
	 */
	public enum StateCode {
		/** Alabama. **/
		AL,
		/** Alaska. **/
		AK,
		/** Arizona. **/
		AZ,
		/** Arkansas. **/
		AR,
		/** California. **/
		CA,
		/** Colorado. **/
		CO,
		/** Connecticut. **/
		CT,
		/** Delaware. **/
		DE,
		/** Florida. **/
		FL,
		/** Georgia. **/
		GA,
		/** Hawaii. **/
		HI,
		/** Idaho. **/
		ID,
		/** Illinois. **/
		IL,
		/** Indiana. **/
		IN,
		/** Iowa. **/
		IA,
		/** Kansas. **/
		KS,
		/** Kentucky. **/
		KY,
		/** Louisiana. **/
		LA,
		/** Maine. **/
		ME,
		/** Maryland. **/
		MD,
		/** Massachusetts. **/
		MA,
		/** Michigan. **/
		MI,
		/** Minnesota. **/
		MN,
		/** Mississippi. **/
		MS,
		/** Missouri. **/
		MO,
		/** Montana. **/
		MT,
		/** Nebraska. **/
		NE,
		/** Nevada. **/
		NV,
		/** New_hampshire. **/
		NH,
		/** New_jersey. **/
		NJ,
		/** New_mexico. **/
		NM,
		/** New_york. **/
		NY,
		/** North_carolina. **/
		NC,
		/** North_dakota. **/
		ND,
		/** Ohio. **/
		OH,
		/** Oklahoma. **/
		OK,
		/** Oregon. **/
		OR,
		/** Pennsylvania. **/
		PA,
		/** Rhode_island. **/
		RI,
		/** South_carolina. **/
		SC,
		/** South_dakota. **/
		SD,
		/** Tennessee. **/
		TN,
		/** Texas. **/
		TX,
		/** Utah. **/
		UT,
		/** Vermont. **/
		VT,
		/** Virginia. **/
		VA,
		/** Washington. **/
		WA,
		/** West_virginia. **/
		WV,
		/** Wisconsin. **/
		WI,
		/** Wyoming. **/
		WY;
	}

	/**
	 * 
	 * @author steffan
	 * 
	 */
	public static class Country {

		private int colorLevel;
		private String code;

		/**
		 * Constructs a country for the {@link Map}.
		 * 
		 * @param name
		 *            {@link CountryCode} the country to show
		 * 
		 * @param colorLevel
		 *            colorLevel a value between 0 and 100, the integer you
		 *            provide here will be interpolated with the color gradient
		 *            that you defined in {@link Map#addChartColor(ChartColor)}
		 * 
		 * @throws IllegalArgumentException
		 *             if name is {@code null}
		 * @throws IllegalArgumentException
		 *             colorLevel can not be < 0 or > 100
		 */
		public Country(CountryName name, int colorLevel) {

			if (name == null)
				throw new IllegalArgumentException("name can not be null");
			if (colorLevel < 0 || colorLevel > 100)
				throw new IllegalArgumentException(
						"colorLevel can not be < 0 oder > 100");

			this.colorLevel = colorLevel;
			this.code = name.getCountryCode();
		}

		/**
		 * Constructs a country for the {@link Map}.
		 * 
		 * @param code
		 * @param colorLevel
		 *            colorLevel a value between 0 and 100, the integer you
		 *            provide here will be interpolated with the color gradient
		 *            that you defined in {@link Map#addChartColor(ChartColor)}
		 * 
		 * @throws IllegalArgumentException
		 *             if code is {@code null}
		 * @throws IllegalArgumentException
		 *             colorLevel can not be < 0 or > 100
		 */
		public Country(CountryCode code, int colorLevel) {

			if (code == null)
				throw new IllegalArgumentException("code can not be null");
			if (colorLevel < 0 || colorLevel > 100)
				throw new IllegalArgumentException(
						"colorLevel can not be < 0 oder > 100");

			this.colorLevel = colorLevel;
			this.code = code.toString();
		}

		/**
		 * Offers you the possibility to use a string for the country code, use
		 * the enum, it is safer.
		 * 
		 * @param code
		 *            e.g. DE for Germany
		 * @param colorLevel
		 *            colorLevel colorLevel a value between 0 and 100, the
		 *            integer you provide here will be interpolated with the
		 *            color gradient that you defined in
		 *            {@link Map#addChartColor(ChartColor)}
		 * 
		 * 
		 * @throws IllegalArgumentException
		 *             if code is {@code null}
		 * @throws IllegalArgumentException
		 *             colorLevel can not be < 0 or > 100
		 */
		public Country(String code, int colorLevel) {

			if (code == null)
				throw new IllegalArgumentException("code can not be null");
			if (colorLevel < 0 || colorLevel > 100)
				throw new IllegalArgumentException(
						"colorLevel can not be < 0 oder > 100");

			this.colorLevel = colorLevel;
			this.code = code;
		}

		/**
		 * Returns the color level, a value between 0 and 100 to interpolate the color.
		 * 
		 * @return the colorLevel
		 */
		public int getColorLevel() {
			return colorLevel;
		}

		/**
		 * Sets the colot level, a value between 0 and 100 to interpolate the color.
		 * @param colorLevel
		 *            the colorLevel to set
		 * 
		 * @throws IllegalArgumentException
		 *             colorLevel can not be < 0 or > 100
		 */
		public void setColorLevel(int colorLevel) {

			if (colorLevel < 0 || colorLevel > 100)
				throw new IllegalArgumentException(
						"colorLevel can not be < 0 oder > 100");
			this.colorLevel = colorLevel;
		}

		/**
		 * Returns the {@link CountryCode}.
		 * 
		 * @return the country code
		 */
		public String getCode() {
			return code;
		}
	}

	/**
	 * Provides an object for the {@link Map#Map(Dimension, List)} contructor.
	 * 
	 * @author steffan
	 * 
	 * 
	 */
	public static class State {

		private int colorLevel;
		private String code;

		/**
		 * Constructs a new state for the {@link Map}.
		 * 
		 * @param name
		 *            {@link StateName} the to show
		 * @param colorLevel
		 *            a value between 0 and 100, the integer you provide here
		 *            will be interpolated with the color gradient that you
		 *            defined in {@link Map#addChartColor(ChartColor)}
		 * 
		 * @throws IllegalArgumentException
		 *             if name is {@code null}
		 * @throws IllegalArgumentException
		 *             colorLevel can not be < 0 or > 100
		 */
		public State(StateName name, int colorLevel) {

			if (name == null)
				throw new IllegalArgumentException("name can not be null");
			if (colorLevel < 0 || colorLevel > 100)
				throw new IllegalArgumentException(
						"colorLevel can not be < 0 oder > 100");

			this.colorLevel = colorLevel;
			this.code = name.getStateCode();

		}

		/**
		 * Constructs new state fpr the {@link Map}.
		 * 
		 * @param name
		 *            {@link StateName} the to show
		 * @param colorLevel
		 *            a value between 0 and 100, the integer you provide here
		 *            will be interpolated with the color gradient that you
		 *            defined in {@link Map#addChartColor(ChartColor)}
		 * 
		 * @throws IllegalArgumentException
		 *             if code is {@code null}
		 * @throws IllegalArgumentException
		 *             colorLevel can not be < 0 or > 100
		 */
		public State(StateCode code, int colorLevel) {

			if (code == null)
				throw new IllegalArgumentException("code can not be null");
			if (colorLevel < 0 || colorLevel > 100)
				throw new IllegalArgumentException(
						"colorLevel can not be < 0 oder > 100");

			this.colorLevel = colorLevel;
			this.code = code.toString();
		}

		/**
		 * The construtor accepts a string for the state code, better use the
		 * enum, it its safer.
		 * 
		 * @param code
		 *            e.g. CA for california
		 * @param colorLevel
		 *            colorLevel a value between 0 and 100, the integer you
		 *            provide here will be interpolated with the color gradient
		 *            that you defined in {@link Map#addChartColor(ChartColor)}
		 * 
		 * @throws IllegalArgumentException
		 *             if code is {@code null}
		 * @throws IllegalArgumentException
		 *             colorLevel can not be < 0 or > 100
		 */
		public State(String code, int colorLevel) {

			if (code == null)
				throw new IllegalArgumentException("code can not be null");
			if (colorLevel < 0 || colorLevel > 100)
				throw new IllegalArgumentException(
						"colorLevel can not be < 0 oder > 100");

			this.colorLevel = colorLevel;
			this.code = code;

		}

		/**
		 * Returns the color level, a value between 0 and 100 to interpolate the color.
		 * 
		 * @return the colorLevel
		 */
		public int getColorLevel() {
			return colorLevel;
		}

		/**
		 * Sets the color level, a value between 0 and 100 to interpolate the color.
		 * 
		 * @param colorLevel
		 *            the colorLevel to set
		 * 
		 * @throws IllegalArgumentException
		 *             colorLevel can not be < 0 or > 100
		 */
		public void setColorLevel(int colorLevel) {

			if (colorLevel < 0 || colorLevel > 100)
				throw new IllegalArgumentException(
						"colorLevel can not be < 0 oder > 100");

			this.colorLevel = colorLevel;
		}

		/**
		 * @return the code the state code convertet to string
		 */
		public String getCode() {
			return code;
		}
	}
}
