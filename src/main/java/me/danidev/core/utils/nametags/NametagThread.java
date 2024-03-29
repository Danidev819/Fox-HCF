package me.danidev.core.utils.nametags;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NametagThread extends Thread {

	private Nametag handler;
	private int interval;

	public NametagThread(Nametag handler, int interval) {
		setName("Nametag-Library");

		this.handler = handler;
		this.interval = interval;
	}

	@Override
	public void run() {
		while (true) {
			try {
				for (Player localPlayer : Bukkit.getOnlinePlayers()) {
					if (localPlayer != null && localPlayer.isOnline()) {
						handler.update(localPlayer);
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			try {
				sleep(50 * interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
