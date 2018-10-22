/**
 *
 */
package com.simple2secure.commons.messages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;

public class MessageCodeUtil {

	private final static Logger log = LoggerFactory.getLogger(MessageCodeUtil.class);

	private static IMessageConveyor defaultMessageConveyor = new MessageConveyor(Locale.GERMAN);

	private static ThreadLocal<IMessageConveyor> threadMC = new ThreadLocal<IMessageConveyor>();

	private static Map<Locale, IMessageConveyor> mcCache = new HashMap<Locale, IMessageConveyor>();

	/**
	 * The default message conveyor
	 *
	 * @return the mc_default
	 */
	public static IMessageConveyor getDefaultMessageConveyor() {
		return defaultMessageConveyor;
	}

	/**
	 * The default message conveyor
	 *
	 * @param mcDefault
	 *          the mc_default to set
	 */
	public static void setDefaultMessageConveyor(IMessageConveyor mcDefault) {
		defaultMessageConveyor = mcDefault;
	}

	/**
	 * Set the locale language string (see {@link Locale}) to use for messages in this processing step (thread).<br>
	 * Use {@link #removeThreadLocaleLocale()} or <code>null</code> argument to reset to default.
	 *
	 * @param localeString
	 *          locale String to set or <code>null</code> to remove
	 */
	public static void setThreadLocalLocale(String localeString) {
		if (localeString == null) {
			threadMC.remove();
		} else {
			IMessageConveyor mc = getMCForLocale(parseLocaleString(localeString));
			threadMC.set(mc);
		}
	}

	/**
	 * Parse the given localeString into a {@link java.util.Locale}.
	 *
	 * This is the inverse operation of {@link java.util.Locale#toString Locale's toString}.
	 *
	 * @param localeString
	 *          the locale string
	 * @return a corresponding Locale instance
	 */
	private static Locale parseLocaleString(String localeString) {
		StringTokenizer parts = new StringTokenizer(localeString, "_ "); //$NON-NLS-1$
		String language = (parts.countTokens() > 0 ? parts.nextToken() : ""); //$NON-NLS-1$
		String country = (parts.countTokens() > 1 ? parts.nextToken() : ""); //$NON-NLS-1$
		String variant = ""; //$NON-NLS-1$
		if (parts.countTokens() >= 2) {
			// There is definitely a variant, and it is everything after the country
			// code sans the separator between the country code and the variant.
			int endIndexOfCountryCode = localeString.indexOf(country) + country.length();
			// Strip off any leading '_' and whitespace, what's left is the variant.
			variant = localeString.substring(endIndexOfCountryCode).trim();
			if (variant.startsWith("_")) { //$NON-NLS-1$
				variant = variant.substring(1).trim();
			}
		}
		return (language.length() > 0 ? new Locale(language, country, variant) : null);
	}

	/**
	 * Remove the message locale setting for this thread.
	 */
	public static void removeThreadLocaleLocale() {
		threadMC.remove();
	}

	private static IMessageConveyor getThreadLocalLocale() {
		IMessageConveyor mc = threadMC.get();
		if (mc != null) {
			return mc;
		}
		return defaultMessageConveyor;
	}

	private static synchronized IMessageConveyor getMCForLocale(Locale l) {
		if (!mcCache.containsKey(l)) {
			IMessageConveyor mc = new MessageConveyor(l);
			mcCache.put(l, mc);
			return mc;
		}
		return mcCache.get(l);
	}

	/**
	 *
	 * @param messageCode
	 * @return the message code message from the given message code
	 */
	public static String getMessageCodeMessage(MessageCode messageCode) {
		try {
			return getThreadLocalLocale().getMessage((Enum<?>) messageCode);
		} catch (Exception e) {
			return handleGetMessageException(messageCode, e);
		}
	}

	/**
	 *
	 * @param messageCode
	 * @param locale
	 * @return the message code message from the given message code and locale
	 */
	public static String getMessageCodeMessage(MessageCode messageCode, Locale locale) {
		try {
			return getMCForLocale(locale).getMessage((Enum<?>) messageCode);
		} catch (Exception e) {
			return handleGetMessageException(messageCode, e);
		}
	}

	/**
	 * @param messageCode
	 * @param params
	 * @return message code message from the given message code. also inserts the parameters
	 */
	public static String getMessageCodeMessage(MessageCode messageCode, String... params) {
		try {
			return getThreadLocalLocale().getMessage((Enum<?>) messageCode, (Object[]) params);
		} catch (Exception e) {
			return handleGetMessageException(messageCode, e);
		}
	}

	/**
	 * @param messageCode
	 * @param params
	 * @param locale
	 * @return message code message from the given message code. also inserts the parameters
	 */
	public static String getMessageCodeMessage(MessageCode messageCode, String[] params, Locale locale) {
		try {
			return getMCForLocale(locale).getMessage((Enum<?>) messageCode, (Object[]) params);
		} catch (Exception e) {
			return handleGetMessageException(messageCode, e);
		}
	}

	/**
	 * @param messageCode
	 * @param e
	 * @return
	 */
	private static String handleGetMessageException(MessageCode messageCode, Exception e) {
		log.error("ERROR resolving Message " + messageCode.toString(), e); //$NON-NLS-1$
		return messageCode.toString() + "Message missing"; //$NON-NLS-1$
	}



	/**
	 * Returns if a message code equals a set of codes to match
	 *
	 * @param messageCode
	 * @param codesToMatch
	 * @return
	 */
	public static boolean equalsMessageCode(MessageCode messageCode, MessageCode... codesToMatch) {
		for (MessageCode code : codesToMatch) {
			if (code.equals(messageCode)) {
				return true;
			}
		}
		return false;
	}
}
