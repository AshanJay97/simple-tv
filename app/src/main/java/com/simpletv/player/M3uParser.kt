package com.simpletv.player

/**
 * Minimal parser for extended M3U (#EXTM3U) playlists.
 * Reads tvg-logo, group-title and the channel display name, plus the stream URL.
 */
object M3uParser {
    private val logoRe = Regex("tvg-logo=\"([^\"]*)\"")
    private val groupRe = Regex("group-title=\"([^\"]*)\"")

    fun parse(text: String): List<Channel> {
        val out = ArrayList<Channel>()
        var name: String? = null
        var logo: String? = null
        var group: String? = null

        for (line in text.lineSequence()) {
            val raw = line.trim()
            if (raw.isEmpty()) continue

            if (raw.startsWith("#EXTINF", ignoreCase = true)) {
                logo = logoRe.find(raw)?.groupValues?.getOrNull(1)
                group = groupRe.find(raw)?.groupValues?.getOrNull(1)
                val comma = raw.lastIndexOf(',')
                name = if (comma in 0 until raw.length - 1) {
                    raw.substring(comma + 1).trim()
                } else {
                    "Channel"
                }
            } else if (raw.startsWith("#")) {
                // Other directives (#EXTVLCOPT, #EXTGRP, etc.) are ignored.
                if (raw.startsWith("#EXTGRP:", ignoreCase = true)) {
                    group = raw.substring(8).trim()
                }
            } else {
                // A non-comment line is the stream URL for the pending channel.
                if (name != null) {
                    out.add(
                        Channel(
                            name = name!!,
                            url = raw,
                            logo = logo?.ifBlank { null },
                            group = group?.ifBlank { null }
                        )
                    )
                }
                name = null
                logo = null
                group = null
            }
        }
        return out
    }
}
