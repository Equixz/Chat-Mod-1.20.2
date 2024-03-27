package me.equixz.chatmood.structure

import java.io.File

object ListFilesInFolder {
    fun listFilesWithoutExtension(folderPath: String?): List<String> {
        val fileNamesWithoutExtension: MutableList<String> = ArrayList()

        // Create a File object for the folder
        val folder = folderPath?.let { File(it) }

        // Check if the path is a directory
        if (folder != null) {
            if (folder.isDirectory) {
                // Get the list of files in the folder
                val files = folder.listFiles()

                // Check if there are any files
                if (files != null) {
                    for (file in files) {
                        // Get the file name without extension
                        val fileNameWithoutExtension = getFileNameWithoutExtension(file)
                        fileNamesWithoutExtension.add(fileNameWithoutExtension)
                    }
                }
            }
        }

        return fileNamesWithoutExtension
    }

    private fun getFileNameWithoutExtension(file: File): String {
        val fileName = file.name
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex != -1) {
            fileName.substring(0, lastDotIndex)
        } else {
            fileName
        }
    }
}