package configfile.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigFileUtil
{
	public static void ParseConfigFile(File configFile, Class<?> thisClass)
	{
		if (thisClass != null)
			ParseConfigFileHelper(configFile, null, thisClass);
	}

	public static void ParseConfigFile(File configFile, Object thisObject)
	{
		if (thisObject != null && thisObject.getClass() != null)
			ParseConfigFileHelper(configFile, thisObject, thisObject.getClass());
	}

	private static void ParseConfigFileHelper(File configFile, Object thisObject, Class<?> thisClass)
	{
		FileReader tempReader = null;
		try {
			tempReader = new FileReader(configFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if (tempReader == null)
		{
			return;
		}

		BufferedReader reader = new BufferedReader(tempReader);

		String currentLine = "";
		String fieldName = "";
		String valueAsString = "";
		
		Matcher matcher = null;
		Field currentField = null;

		Pattern pattern = Pattern.compile("(\\w+)(\\s*=\\s*)(.*)");

		try {
			while ((currentLine = reader.readLine()) != null)
			{
				matcher = pattern.matcher(currentLine);

				if (!matcher.matches())
					continue;

				fieldName = matcher.group(1);
				valueAsString = matcher.group(3);

				currentField = null;
				try {
					currentField = thisClass.getDeclaredField(fieldName);
				} catch (NoSuchFieldException | SecurityException e) {
					e.printStackTrace();
				}

				if (currentField != null) {

					Class<?> fieldClass = currentField.getType();

					// System.out.printf("%s is type %s", fieldName,
					// currentField.getType().toString());

					if (fieldClass.equals(byte.class))
					{
						byte byteValue;
						try {
							byteValue = Byte.parseByte(valueAsString);
						} catch (NumberFormatException e) {
							continue;
						}
						SetFieldValue(currentField, thisObject, byteValue);
					}
					else if (fieldClass.equals(short.class))
					{
						short shortValue;
						try {
							shortValue = Short.parseShort(valueAsString);
						} catch (NumberFormatException e) {
							continue;
						}
						SetFieldValue(currentField, thisObject, shortValue);
					}
					else if (fieldClass.equals(int.class))
					{
						int intValue;
						try {
							intValue = Integer.parseInt(valueAsString);
						} catch (NumberFormatException e) {
							continue;
						}
						SetFieldValue(currentField, thisObject, intValue);
					}
					else if (fieldClass.equals(long.class))
					{
						long longValue;
						try {
							longValue = Long.parseLong(valueAsString);
						} catch (NumberFormatException e) {
							continue;
						}
						SetFieldValue(currentField, thisObject, longValue);
					}
					else if (fieldClass.equals(float.class))
					{
						float floatValue;
						try {
							floatValue = Float.parseFloat(valueAsString);
						} catch (NumberFormatException e) {
							continue;
						}
						SetFieldValue(currentField, thisObject, floatValue);
					}
					else if (fieldClass.equals(double.class))
					{
						double doubleValue;
						try {
							doubleValue = Double.parseDouble(valueAsString);
						} catch (NumberFormatException e) {
							continue;
						}
						SetFieldValue(currentField, thisObject, doubleValue);
					}
					else if (fieldClass.equals(boolean.class))
					{
						boolean booleanValue = Boolean.parseBoolean(valueAsString);
						SetFieldValue(currentField, thisObject, booleanValue);
					}
					else if (fieldClass.equals(char.class))
					{
						char charValue = valueAsString.charAt(0);
						SetFieldValue(currentField, thisObject, charValue);
					}
					else if (fieldClass.equals(String.class))
					{
						SetFieldValue(currentField, thisObject, valueAsString);
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			reader.close();
			tempReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void SetFieldValue(Field thisField, Object thisObject, Object value)
	{
		// Comment this line in if you want, but as it is now,
		// thisField is null checked before this function is called.
		//if (thisField == null) return;
		
		boolean originallyAccessible = thisField.isAccessible();
		
		if (!originallyAccessible)
		{
			try {
				// Set field accessible if it's not
				thisField.setAccessible(true);
			} catch (SecurityException e) {
				e.printStackTrace();
				return;
			}
		}

		try {
			// Set the field value
			thisField.set(thisObject, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		if (!originallyAccessible)
		{
			try {
				// Set field to its original accessibility
				thisField.setAccessible(originallyAccessible);
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}
}
