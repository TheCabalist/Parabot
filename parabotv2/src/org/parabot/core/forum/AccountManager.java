package org.parabot.core.forum;

import org.parabot.core.Configuration;
import org.parabot.core.Core;
import org.parabot.core.parsers.scripts.BDNScripts;
import org.parabot.core.parsers.servers.PublicServers;
import org.parabot.core.ui.components.VerboseLoader;
import org.parabot.environment.api.utils.WebUtil;
import org.parabot.environment.scripts.executers.BDNScriptsExecuter;
import org.parabot.environment.servers.executers.PublicServerExecuter;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Handles logging in to parabot forum, only certain classes may use this class.
 * 
 * @author Everel
 * 
 */
public final class AccountManager {
	private static boolean validated;
	private static AccountManager instance;

	private Account account;

	private AccountManager() {

	}

	public static final void validate() {
		if (validated) {
			return;
		}
		validated = true;
		instance = new AccountManager();

		Core.verbose("Initializing account manager accessors...");
		final ArrayList<AccountManagerAccess> accessors = new ArrayList<AccountManagerAccess>();
		accessors.add(BDNScripts.MANAGER_FETCHER);
		accessors.add(VerboseLoader.MANAGER_FETCHER);
		accessors.add(BDNScriptsExecuter.MANAGER_FETCHER);
		accessors.add(PublicServers.MANAGER_FETCHER);
		accessors.add(PublicServerExecuter.MANAGER_FETCHER);

		for (final AccountManagerAccess accessor : accessors) {
			accessor.setManager(instance);
		}
		Core.verbose("Account managers initialized.");
	}
	
	public final boolean isLoggedIn() {
		return account != null;
	}
	
	public final Account getAccount() {
		return account;
	}

	public final boolean login(final String user, final String pass) {
		if (account != null) {
			throw new IllegalStateException("Already logged in.");
		}
		String contents;
		try {
			contents = WebUtil.getContents(String.format(
                    Configuration.LOGIN_SERVER,
                    URLEncoder.encode(user, "UTF-8"),
                    URLEncoder.encode(pass, "UTF-8")));
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}

		if (contents.equals("correct")) {
            account = new Account(user, pass);
            return true;
		}
		return false;
	}

}