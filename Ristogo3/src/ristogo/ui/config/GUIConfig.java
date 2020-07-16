package ristogo.ui.config;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ristogo.config.Configuration;

public class GUIConfig
{
	private static Configuration config = Configuration.getConfig();

	private static double getFontSizeVeryTiny()
	{
		return config.getFontSize() - 4;
	}

	private static double getFontSizeTiny()
	{
		return config.getFontSize() - 2;
	}

	private static double getFontSizeSmall()
	{
		return config.getFontSize() - 1;
	}

	private static double getFontSizeNormal()
	{
		return config.getFontSize();
	}

	private static double getFontSizeLarge()
	{

		return config.getFontSize() + 1;
	}

	private static double getFontSizeBig()
	{

		return config.getFontSize() + 2;
	}

	private static double getFontSizeHuge()
	{
		return config.getFontSize() + 5;
	}

	private static double getTitleFontSize()
	{
		return getFontSizeHuge();
	}

	private static double getWelcomeFontSize()
	{
		return getFontSizeBig();
	}

	private static double getFormTitleFontSize()
	{
		return getFontSizeLarge();
	}

	private static double getTableTextFontSize()
	{
		return getFontSizeNormal();
	}

	private static double getFormSubtitleFontSize()
	{
		return getFontSizeSmall();
	}

	private static double getTextFontSize()
	{
		return getFontSizeTiny();
	}

	private static double getVeryTinyTextFontSize()
	{

		return getFontSizeVeryTiny();
	}

	private static double getButtonFontSize()
	{
		return getFontSizeNormal();
	}

	private static double getDialogLabelFontSize()
	{
		return getFontSizeBig();
	}

	private static double getUsernameFontSize()
	{
		return getFontSizeBig();
	}

	public static Font getTitleFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getTitleFontSize());
	}

	public static Font getFormTitleFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getFormTitleFontSize());
	}

	public static Font getFormSubtitleFont()
	{
		return Font.font(config.getFontName(), FontWeight.NORMAL, getFormSubtitleFontSize());
	}

	public static Font getTextFont()
	{
		return Font.font(config.getFontName(), FontWeight.NORMAL, getTextFontSize());
	}

	public static Font getBoldVeryTinyTextFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getVeryTinyTextFontSize());
	}

	public static Font getButtonFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getButtonFontSize());
	}

	public static Font getDialogLabelFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getDialogLabelFontSize());
	}

	public static Font getFormLabelFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getTextFontSize());
	}

	public static Font getUsernameFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getUsernameFontSize());
	}

	public static Font getWelcomeFont()
	{
		return Font.font(config.getFontName(), FontWeight.NORMAL, getWelcomeFontSize());
	}

	public static Color getFgColor()
	{
		return Color.web(config.getFgColorName());
	}

	public static Color getBgColor()
	{
		return Color.web(config.getBgColorName());
	}

	public static Color getInvertedFgColor()
	{
		return getBgColor();
	}

	public static Color getDialogFgColor()
	{
		return getInvertedFgColor();
	}

	private static String getCSSBgColor()
	{
		return "-fx-background-color: " + config.getBgColorName() + ";";
	}

	private static String getCSSFgColor()
	{
		return "-fx-text-fill: " + config.getFgColorName() + ";";
	}

	private static String getInvertedCSSBgColor()
	{
		return "-fx-background-color: " + config.getFgColorName() + ";";
	}

	private static String getInvertedCSSFgColor()
	{
		return "-fx-text-fill: " + config.getBgColorName() + ";";
	}

	private static String getCSSPadding()
	{
		return "-fx-padding: 7;";
	}

	private static String getCSSBorderStyle()
	{
		return "-fx-border-style: solid inside;";
	}

	private static String getCSSBorderDim()
	{
		return "-fx-border-width: 2; -fx-border-insets: 3; -fx-border-radius: 10;";
	}

	private static String getCSSBorderColor()
	{
		return "-fx-border-color: " + config.getFgColorName() + ";";
	}

	private static String getCSSFontFamily()
	{
		return "-fx-font-family: " + config.getFontName() + ";";
	}

	private static String getCSSButtonFontSize()
	{
		return "-fx-font-size: " + getButtonFontSize() + "px;";
	}

	private static String getCSSTableTextFontSize()
	{
		return "-fx-font-size: " + getTableTextFontSize() + "px;";
	}

	public static String getInvertedCSSButtonBgColor()
	{
		return "-fx-base: " + config.getFgColorName() + ";";
	}

	public static String getCSSFormTitleStyle()
	{
		return "-fx-underline: true;";
	}

	public static String getCSSDialogBgColor()
	{
		return getInvertedCSSBgColor();
	}

	public static String getCSSDialogFgColor()
	{
		return getInvertedCSSFgColor();
	}

	public static String getCSSDialogButtonStyle()
	{
		return getCSSFontFamily() + getCSSBgColor() + getCSSFgColor() + getCSSButtonFontSize();
	}

	public static String getCSSDialogHeaderStyle()
	{
		return getCSSFontFamily() + getInvertedCSSBgColor() + getInvertedCSSFgColor() + "-fx-wrap-text: true;";
	}

	public static String getCSSTableColumnStyle(boolean alignCenter)
	{
		return getCSSFontFamily() + getCSSTableTextFontSize() + getCSSFgColor() + (alignCenter ? "-fx-alignment: CENTER;" : "");
	}

	public static String getCSSTableColumnStyle()
	{
		return getCSSTableColumnStyle(true);
	}

	public static String getCSSFormBoxStyle()
	{
		return getCSSPadding() + getCSSBorderDim() + getCSSBorderStyle() + getCSSBorderColor();
	}

	public static String getCSSInterfacePartStyle()
	{
		return getCSSPadding() + getCSSBorderDim();
	}

	public static int getMaxRowDisplayable(boolean isOwner)
	{
		return config.getNumberRowsDisplayable() - (isOwner ? 0 : 2);
	}

	public static int getMaxRowDisplayable()
	{
		return getMaxRowDisplayable(true);
	}
}
