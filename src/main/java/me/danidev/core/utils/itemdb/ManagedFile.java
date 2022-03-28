package me.danidev.core.utils.itemdb;

import me.danidev.core.Main;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class ManagedFile {

    private final transient File file;

    public ManagedFile(final String filename, final JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), filename);
        if (!this.file.exists()) {
            try {
                copyResourceAscii('/' + filename, this.file);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "items.csv has not been loaded", ex);
            }
        }
    }

    public static void copyResourceAscii(final String resourceName, final File file) throws IOException {
        Throwable t = null;
        try {
            final InputStreamReader reader = new InputStreamReader(ManagedFile.class.getResourceAsStream(resourceName),
                    StandardCharsets.UTF_8);
            try {
                final MessageDigest digest = getDigest();
                Throwable t2 = null;
                Label_0266:
                {
                    try {
                        final DigestOutputStream digestStream = new DigestOutputStream(new FileOutputStream(file),
                                digest);
                        try {
                            final OutputStreamWriter writer = new OutputStreamWriter(digestStream,
                                    StandardCharsets.UTF_8);
                            try {
                                final char[] buffer = new char[8192];
                                int length;
                                while ((length = reader.read(buffer)) >= 0) {
                                    writer.write(buffer, 0, length);
                                }
                                writer.write("\n");
                                writer.flush();
                                digestStream.on(false);
                                digestStream.write(35);
                                digestStream.write(new BigInteger(1, digest.digest()).toString(16)
                                        .getBytes(StandardCharsets.UTF_8));
                            } finally {
                                if (writer != null) {
                                    writer.close();
                                }
                            }
                            if (digestStream != null) {
                                digestStream.close();
                                break Label_0266;
                            }
                            break Label_0266;
                        } finally {
                            if (t2 == null) {
                                final Throwable t3 = null;
                                t2 = t3;
                            } else {
                                final Throwable t3 = null;
                                if (t2 != t3) {
                                    t2.addSuppressed(t3);
                                }
                            }
                            if (digestStream != null) {
                                digestStream.close();
                            }
                        }
                    } finally {
                    }
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } finally {
            if (t == null) {
                final Throwable t4 = null;
                t = t4;
            } else {
                final Throwable t4 = null;
                if (t != t4) {
                    t.addSuppressed(t4);
                }
            }
        }
    }

    public static MessageDigest getDigest() throws IOException {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

    public File getFile() {
        return this.file;
    }

    public List<String> getLines() {
        try {
            Throwable t = null;
            try {
                final BufferedReader reader = Files.newBufferedReader(Paths.get(this.file.getPath()),
                        StandardCharsets.UTF_8);
                try {
                    final List<String> lines = new ArrayList<String>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                    return lines;
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            } finally {
                if (t == null) {
                    final Throwable t2 = null;
                    t = t2;
                } else {
                    final Throwable t2 = null;
                    if (t != t2) {
                        t.addSuppressed(t2);
                    }
                }
            }
        } catch (IOException ex) {
            Main.get().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
}
