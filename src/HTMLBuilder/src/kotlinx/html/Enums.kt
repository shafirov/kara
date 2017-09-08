package kotlinx.html

import kotlin.reflect.KClass

private interface EnumWithValue {
    val value : String
}

private inline fun <reified T> KClass<T>.hasValue(s: String) where T : Enum<T>, T: EnumWithValue =
        enumValues<T>().any { it.value == s && it.value != "inherit" }

private inline fun <reified T> KClass<T>.makeValue(s: String) where T : Enum<T>, T: EnumWithValue =
        enumValues<T>().find { it.value == s && it.value != "inherit" } ?: error("Unknown ${this.simpleName} value $s")

enum class BackgroundAttachment(override val value: String) : EnumWithValue {
    scroll("scroll"),
    fixed("fixed"),
    inherit("inherit");
    
    override fun toString(): String = value
}

fun isBackgroundAttachment(s: String) = BackgroundAttachment::class.hasValue(s)

fun makeBackgroundAttachment(s: String) = BackgroundAttachment::class.makeValue(s)

enum class BackgroundRepeat(override val value: String) : EnumWithValue {
    repeat("repeat"),
    repeatX("repeat-x"),
    repeatY("repeat-y"),
    noRepeat("no-repeat"),
    inherit("inherit");

    override fun toString(): String = value
}

fun isBackgroundRepeat(s: String): Boolean = BackgroundRepeat::class.hasValue(s)

fun makeBackgroundRepeat(s: String): BackgroundRepeat = BackgroundRepeat::class.makeValue(s)

enum class BorderStyle(override val value: String) : EnumWithValue {
    none("none"),
    hidden("hidden"),
    dotted("dotted"),
    dashed("dashed"),
    solid("solid"),
    double("double"),
    groove("groove"),
    ridge("ridge"),
    inset("inset"),
    outset("outset"),
    inherit("inherit");

    override fun toString(): String = value
}

fun isBorderStyle(s: String): Boolean = BorderStyle::class.hasValue(s)

fun makeBorderStyle(s: String): BorderStyle = BorderStyle::class.makeValue(s)

enum class BoxDirection(override val value: String) : EnumWithValue {
    normal("normal"),
    reverse("reverse"),
    inherit("inherit");
    
    override fun toString(): String = value
}

fun isBoxDirection(s: String): Boolean = BoxDirection::class.hasValue(s)
fun makeBoxDirection(s: String): BoxDirection = BoxDirection::class.makeValue(s)

enum class BoxAlign(override val value: String) : EnumWithValue {
    start("start"),
    end("end"),
    center("center"),
    baseline("baseline"),
    inherit("inherit");
    override fun toString(): String = value
        
}
fun isBoxAlign(s: String): Boolean = BoxAlign::class.hasValue(s)
fun makeBoxAlign(s: String): BoxAlign = BoxAlign::class.makeValue(s)

enum class BoxLines(override val value: String) : EnumWithValue {
    single("single"),
    multiple("multiple"),
    inherit("inherit");

    override fun toString(): String = value
}
fun isBoxLines(s: String): Boolean = BoxLines::class.hasValue(s)
    
fun makeBoxLines(s: String): BoxLines = BoxLines::class.makeValue(s)

enum class BoxOrient(override val value: String) : EnumWithValue {
    horizontal("horizontal"),
    vertical("vertical"),
    inlineAxis("inline-axis"),
    blockAxis("block-axis"),
    inherit("inherit");
    override fun toString(): String = value
}
fun isBoxOrient(s: String): Boolean = BoxOrient::class.hasValue(s)
fun makeBoxOrient(s: String): BoxOrient = BoxOrient::class.makeValue(s)

enum class BoxPack(override val value: String) : EnumWithValue{
    start("start"),
    end("end"),
    center("center"),
    inherit("inherit");
    override fun toString() = value
}

fun isBoxPack(s: String): Boolean = BoxPack::class.hasValue(s)
fun makeBoxPack(s: String): BoxPack = BoxPack::class.makeValue(s)

enum class BoxSizing(override val value: String) : EnumWithValue {
    contentBox("content-box"),
    borderBox("border-box"),
    inherit("inherit");
    override fun toString() = value
}
fun isBoxSizing(s: String): Boolean = BoxSizing::class.hasValue(s)
fun makeBoxSizing(s: String): BoxSizing = BoxSizing::class.makeValue(s)

enum class CaptionSide(override val value: String) : EnumWithValue {
    top("top"),
    bottom("bottom"),
    inherit("inherit");
    override fun toString() = value
}
fun isCaptionSide(s: String): Boolean = CaptionSide::class.hasValue(s)
fun makeCaptionSide(s: String): CaptionSide = CaptionSide::class.makeValue(s)

enum class Clear(override val value: String) : EnumWithValue {
    left("left"),
    right("right"),
    both("both"),
    none("none"),
    inherit("inherit");
    override fun toString() = value
}
fun isClear(s: String): Boolean = Clear::class.hasValue(s)
fun makeClear(s: String): Clear = Clear::class.makeValue(s)

enum class ColumnFill(override val value: String) : EnumWithValue {
    balance("balance"),
    auto("auto"),
    inherit("inherit");
    override fun toString() = value
}
fun isColumnFill(s: String): Boolean = ColumnFill::class.hasValue(s)
fun makeColumnFill(s: String): ColumnFill = ColumnFill::class.makeValue(s)

enum class Direction(override val value: String) : EnumWithValue {
    ltr("ltr"),
    rtl("rtl"),
    inherit("inherit");
    override fun toString() = value
}
fun isDirection(s: String): Boolean = Direction::class.hasValue(s)
fun makeDirection(s: String): Direction = Direction::class.makeValue(s)

enum class Display(override val value: String) : EnumWithValue {
    none("none"),
    block("block"),
    `inline`("inline"),
    inlineBlock("inline-block"),
    inlineTable("inline-table"),
    listItem("list-item"),
    table("table"),
    tableCaption("table-caption"),
    tableCell("table-cell"),
    tableColumn("table-column"),
    tableColumnGroup("table-column-group"),
    tableFooterGroup("table-footer-group"),
    tableHeaderGroup("table-header-group"),
    tableRow("table-row"),
    tableRowGroup("table-row-group"),
    inherit("inherit");
    override fun toString() = value
}
fun isDisplay(s: String): Boolean = Display::class.hasValue(s)
fun makeDisplay(s: String): Display = Display::class.makeValue(s)

enum class EmptyCells(override val value: String) : EnumWithValue {
    hide("hide"),
    show("show"),
    inherit("inherit");
    override fun toString() = value
}
fun isEmptyCells(s: String): Boolean = EmptyCells::class.hasValue(s)
fun makeEmptyCells(s: String): EmptyCells = EmptyCells::class.makeValue(s)

enum class FloatType(override val value: String) : EnumWithValue {
    left("left"),
    right("right"),
    none("none"),
    inherit("inherit");
    override fun toString() = value
}
fun isFloat(s: String): Boolean = FloatType::class.hasValue(s)
fun makeFloat(s: String): FloatType = FloatType::class.makeValue(s)

enum class FontStyle(override val value: String) : EnumWithValue {
    normal("normal"),
    italic("italic"),
    oblique("oblique"),
    inherit("inherit");
    override fun toString() = value
}
fun isFontStyle(s: String): Boolean = FontStyle::class.hasValue(s)
fun makeFontStyle(s: String): FontStyle = FontStyle::class.makeValue(s)

enum class FontVariant(override val value: String) : EnumWithValue {
    normal("normal"),
    smallCaps("small-caps"),
    inherit("inherit");
    override fun toString() = value
}
fun isFontVariant(s: String): Boolean = FontVariant::class.hasValue(s)
fun makeFontVariant(s: String): FontVariant = FontVariant::class.makeValue(s)

enum class FontWeight(override val value: String) : EnumWithValue {
    normal("normal"),
    bold("bold"),
    bolder("bolder"),
    lighter("lighter"),
    inherit("inherit");
    override fun toString() = value
}
fun isFontWeight(s: String): Boolean = FontWeight::class.hasValue(s)
fun makeFontWeight(s: String): FontWeight = FontWeight::class.makeValue(s)

enum class FontStretch(override val value: String) : EnumWithValue {
    wider("wider"),
    narrower("narrower"),
    ultraCondensed("ultra-condensed"),
    extraCondensed("extra-condensed"),
    condensed("condensed"),
    semiCondensed("semi-condensed"),
    normal("normal"),
    semiExpanded("semi-expanded"),
    expanded("expanded"),
    extraExpanded("extra-expanded"),
    ultraExpanded("ultra-expanded"),
    inherit("inherit");
    override fun toString() = value
}
fun isFontStretch(s: String): Boolean = FontStretch::class.hasValue(s)
fun makeFontStretch(s: String): FontStretch = FontStretch::class.makeValue(s)

enum class ListStyleType(override val value: String) : EnumWithValue {
    circle("circle"),
    disc("disc"),
    decimal("decimal"),
    lowerAlpha("lower-alpha"),
    lowerGreek("lower-greek"),
    lowerLatin("lower-latin"),
    lowerRoman("lower-roman"),
    none("none"),
    square("square"),
    upperAlpha("upper-alpha"),
    upper("upper"),
    latin("latin"),
    upperRoman("upper-roman"),
    inherit("inherit");
    override fun toString() = value
}
fun isListStyleType(s: String): Boolean = ListStyleType::class.hasValue(s)
fun makeListStyleType(s: String): ListStyleType = ListStyleType::class.makeValue(s)

enum class ListStylePosition(override val value: String) : EnumWithValue {
    inside("inside"),
    outside("outside"),
    inherit("inherit");
    override fun toString() = value
}
fun isListStylePosition(s: String): Boolean = ListStylePosition::class.hasValue(s)
fun makeListStylePosition(s: String): ListStylePosition = ListStylePosition::class.makeValue(s)

enum class Overflow(override val value: String) : EnumWithValue {
    visible("visible"),
    hidden("hidden"),
    scroll("scroll"),
    auto("auto"),
    noDisplay("no-display"),
    noContent("no-content"),
    inherit("inherit");
    override fun toString() = value
}
fun isOverflow(s: String): Boolean = Overflow::class.hasValue(s)
fun makeOverflow(s: String): Overflow  = Overflow::class.makeValue(s)

enum class PageBreak(override val value: String) : EnumWithValue {
    auto("auto"),
    always("always"),
    avoid("avoid"),
    left("left"),
    right("right"),
    inherit("inherit");
    override fun toString() = value
}
fun isPageBreak(s: String): Boolean = PageBreak::class.hasValue(s)
fun makePageBreak(s: String): PageBreak = PageBreak::class.makeValue(s)

enum class Position(override val value: String) : EnumWithValue {
    static("static"),
    absolute("absolute"),
    fixed("fixed"),
    relative("relative"),
    inherit("inherit");
    override fun toString() = value
}
fun isPosition(s: String): Boolean = Position::class.hasValue(s)
fun makePosition(s: String): Position = Position::class.makeValue(s)

enum class Resize(override val value: String) : EnumWithValue {
    none("none"),
    both("both"),
    horizontal("horizontal"),
    vertical("vertical"),
    inherit("inherit");
    override fun toString() = value
}
fun isResize(s: String): Boolean = Resize::class.hasValue(s)
fun makeResize(s: String): Resize = Resize::class.makeValue(s)

enum class TableLayout(override val value: String) : EnumWithValue {
    auto("auto"),
    fixed("fixed"),
    inherit("inherit");
    override fun toString() = value
}
fun isTableLayout(s: String): Boolean = TableLayout::class.hasValue(s)
fun makeTableLayout(s: String): TableLayout = TableLayout::class.makeValue(s)

enum class TextAlign(override val value: String) : EnumWithValue {
    left("left"),
    right("right"),
    center("center"),
    justify("justify"),
    inherit("inherit");
    override fun toString() = value
}
fun isTextAlign(s: String): Boolean = TextAlign::class.hasValue(s)
fun makeTextAlign(s: String): TextAlign = TextAlign::class.makeValue(s)

enum class VerticalAlign(override val value: String) : EnumWithValue {
    top("top"),
    bottom("bottom"),
    middle("middle"),
    inherit("inherit");
    override fun toString() = value
}
fun isVerticalAlign(s: String): Boolean = VerticalAlign::class.hasValue(s)
fun makeVerticalAlign(s: String): VerticalAlign = VerticalAlign::class.makeValue(s)

enum class Visibility(override val value: String) : EnumWithValue {
    visible("visible"),
    hidden("hidden"),
    collapse("collapse"),
    inherit("inherit");
    override fun toString() = value
}
fun isVisibility(s: String): Boolean = Visibility::class.hasValue(s)
fun makeVisibility(s: String): Visibility = Visibility::class.makeValue(s)

enum class WhiteSpace(override val value: String) : EnumWithValue {
    normal("normal"),
    nowrap("nowrap"),
    pre("pre"),
    preLine("pre-line"),
    preWrap("pre-wrap"),
    inherit("inherit");
    override fun toString() = value
}
fun isWhiteSpace(s: String): Boolean = WhiteSpace::class.hasValue(s)
fun makeWhiteSpace(s: String): WhiteSpace = WhiteSpace::class.makeValue(s)

enum class WordBreak(override val value: String) : EnumWithValue {
    normal("normal"),
    breakAll("break-all"),
    hyphenate("hyphenate"),
    inherit("inherit");
    override fun toString() = value
}
fun isWordBreak(s: String): Boolean = WordBreak::class.hasValue(s)
fun makeWordBreak(s: String): WordBreak = WordBreak::class.makeValue(s)

enum class WordWrap(override val value: String) : EnumWithValue {
    normal("normal"),
    breakWord("break-word"),
    inherit("inherit");
    override fun toString() = value
}
fun isWordWrap(s: String): Boolean = WordWrap::class.hasValue(s)
fun makeWordWrap(s: String): WordWrap = WordWrap::class.makeValue(s)

