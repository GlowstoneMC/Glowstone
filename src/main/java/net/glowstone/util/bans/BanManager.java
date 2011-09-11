package net.glowstone.util.bans;

import java.util.Set;

/**
 * Represents a system capable of managing player and IP bans and messages.
 */
public interface BanManager {

    /**
     * Load the ban manager
     */
    public void load();

    /**
     * Check if a  name is banned
     *
     * @param player
     * @return if the name is banned
     */
    public boolean isBanned(String player);

    /**
     * Set a name as banned or unbanned
     *
     * @param player
     * @param banned
     * @return if the name's banned state was changed by this operation
     */
    public boolean setBanned(String player, boolean banned);

    /**
     * Returns a string set of currently banned names
     *
     * @return
     */
    public Set<String> getBans();

    /**
     * Returns the ban message for the provided name
     *
     * @param name
     * @return
     */
    public String getBanMessage(String name);

    /**
     * Check if an address is banned
     *
     * @param address
     * @return if the address is banned
     */
    public boolean isIpBanned(String address);

    /**
     * Set an address as banned or unbanned
     *
     * @param address
     * @param banned
     * @return if the address's banned state was changed by this operation
     */
    public boolean setIpBanned(String address, boolean banned);

    /**
     * Returns a string set of currently banned addresses
     *
     * @return
     */
    public Set<String> getIpBans();

    /**
     * Returns the ban message for the provided address
     *
     * @param address
     * @return
     */
    public String getIpBanMessage(String address);

    /**
     * Return if a name or address is banned
     * 
     * @param player
     * @param address
     * @return
     */
    public boolean isBanned(String player, String address);
    
}
