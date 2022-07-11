package com.cosine.worldcreator.main

import com.cosine.worldcreator.command.OpCommand
import org.bukkit.plugin.java.JavaPlugin

class WorldCreator : JavaPlugin() {

    override fun onEnable() {
        logger.info("월드 생성 플러그인 활성화")

        getCommand("월드").executor = OpCommand()
    }

    override fun onDisable() {
        logger.info("월드 생성 플러그인 비활성화")
    }
}