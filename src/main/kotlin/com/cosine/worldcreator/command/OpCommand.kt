package com.cosine.worldcreator.command

import org.apache.commons.io.FileUtils
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator
import java.io.File
import java.util.*
import kotlin.collections.List


class OpCommand : CommandExecutor {

    private val option = "§6[ 월드 ]§f"

    private val type: List<String> = listOf("야생", "평지", "공허")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender
            if (!player.isOp) return false
            if (args.isEmpty()) {
                help(player)
            }
            when (args[0]) {
                "생성" -> {
                    when (args.size) {
                        1 -> {
                            player.sendMessage("$option 생성하실 월드 이름을 적어주세요.")
                            return false
                        }
                        2 -> {
                            if (checkWorldExist(args[1])) {
                                player.sendMessage("$option 이미 존재하는 월드입니다.")
                                return false
                            }
                            createWorld(args[1], "야생")
                            return false
                        }
                        3 -> {
                            if (checkWorldExist(args[1])) {
                                player.sendMessage("$option 이미 존재하는 월드입니다.")
                                return false
                            }
                            if (!type.contains(args[2])) {
                                player.sendMessage("$option 존재하지 않는 월드 타입입니다.")
                                return false;
                            }
                            createWorld(args[1], args[2])
                        }
                    }
                }
                "제거" -> {
                    if (!checkWorldExist(args[1])) {
                        player.sendMessage("$option 존재하지 않는 월드입니다.")
                        return false
                    }
                    if (checkWorldPlayerExist(args[1])) {
                        player.sendMessage("$option 월드에 플레이어가 존재합니다.")
                        return false
                    }
                    removeWorld(args[1])
                }
                "이동" -> {
                    when (args.size) {
                        1 -> {
                            player.sendMessage("$option 이동할 월드 이름을 적어주세요.")
                            return false
                        }
                        2 -> {
                            if (!checkWorldExist(args[1])) {
                                player.sendMessage("$option 존재하지 않는 월드입니다.")
                                return false
                            }
                            teleportWorld(player, args[1])
                        }
                        3 -> {
                            if (!checkWorldExist(args[1])) {
                                player.sendMessage("$option 존재하지 않는 월드입니다.")
                                return false
                            }
                            if (Bukkit.getPlayer(args[2]) == null) {
                                player.sendMessage("$option 존재하지 않는 플레이어입니다.")
                                return false
                            }
                            teleportWorld(Bukkit.getPlayer(args[2]), args[1])
                        }
                    }
                }
            }
        }
        return false
    }
    private fun help(player: Player) {
        player.sendMessage("$option 월드 생성기 시스템 도움말")
        player.sendMessage("")
        player.sendMessage("$option /월드 생성 [이름] [타입]")
        player.sendMessage("$option /월드 제거 [이름]")
        player.sendMessage("$option /월드 이동 [이름] [닉네임]")
        player.sendMessage("§7[ 타입: 야생, 평지, 공허 ]")
    }
    private fun checkWorldExist(name: String): Boolean {
        if (Bukkit.getWorld(name) != null) {
            return true
        }
        return false
    }
    private fun checkWorldPlayerExist(name: String): Boolean {
        if (Bukkit.getWorld(name).playerCount != 0) {
            return true
        }
        return false
    }
    private fun createWorld(name: String, type: String) {
        val worldCreator = WorldCreator(name)

        when (type) {
            "야생" -> worldCreator.type(WorldType.NORMAL)
            "평지" -> worldCreator.type(WorldType.FLAT)
            "공허" -> {
                worldCreator.type(WorldType.CUSTOMIZED)
                worldCreator.generator(object : ChunkGenerator() {
                    override fun generate(world: World?, random: Random?, x: Int, z: Int): ByteArray {
                        return ByteArray(32768)
                    }
                })
            }
        }
        Bukkit.createWorld(worldCreator)
    }
    private fun removeWorld(name: String) {
        Bukkit.unloadWorld(name, false)
        FileUtils.deleteDirectory(File(name))
    }
    private fun teleportWorld(player: Player, name: String) {
        val world = Bukkit.getWorld(name);
        val location = Location(world, world.spawnLocation.x, world.spawnLocation.y, world.spawnLocation.z, 0F, 0F)
        player.teleport(location)
    }
}