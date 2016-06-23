/*******************************************************************************
 * This file is part of ASkyBlock.
 *
 *     ASkyBlock is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ASkyBlock is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ASkyBlock.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.wasteofplastic.askyblock.listeners;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Converts redstone to obsidian in certain situations: when lava flows into
 * redstone in a way that would make cobble.
 * 
 * @see https://bugs.mojang.com/browse/MC-4239
 * @author Pokechu22
 */
public class RedstoneToObsidian implements Listener {
	private final JavaPlugin plugin;
	private Random rand = new Random();
	public RedstoneToObsidian(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockChange(BlockFromToEvent e) {
		Block fromBlock = e.getBlock();
		Block toBlock = e.getToBlock();
		if (fromBlock.getType() == Material.LAVA
				|| fromBlock.getType() == Material.STATIONARY_LAVA) {
			if (toBlock.getType() == Material.REDSTONE_WIRE) {
				// Check to see if the block would become cobble
				boolean wouldMakeCobble = false;
				for (BlockFace face : BlockFace.values()) {
					if (face == BlockFace.DOWN) { continue; }
					
					Block block = toBlock.getRelative(face);
					if (block.getType() == Material.WATER
							|| block.getType() == Material.STATIONARY_WATER) {
						wouldMakeCobble = true;
						break;
					}
				}
				
				if (wouldMakeCobble) {
					toBlock.setType(Material.OBSIDIAN);
					e.setCancelled(true);
					
					playMixEffect(toBlock.getLocation());
				}
			}
		}
	}
	
	private void playMixEffect(Location location) {
		World world = location.getWorld();
		
		world.playSound(location, Sound.BLOCK_LAVA_EXTINGUISH, .5f,
				2.6f + (rand.nextFloat() - rand.nextFloat()) * 0.8f);
		
		for (int i = 0; i < 8; i++) {
			// Normal smoke
			world.spawnParticle(Particle.SMOKE_LARGE,
					location.getX() + Math.random(), location.getY() + 1.2,
					location.getZ() + Math.random(), 1, 0, 0, 0, 0, null);
			// Bonus purple and red particles
			world.spawnParticle(Particle.PORTAL,
					location.getX() + Math.random(), location.getY() + 1.2,
					location.getZ() + Math.random(), 1, 0, 0, 0, 0, null);
			world.spawnParticle(Particle.REDSTONE,
					location.getX() + Math.random(), location.getY() + 1.2,
					location.getZ() + Math.random(), 1, 0, 0, 0, 0, null);
		}
	}
}
