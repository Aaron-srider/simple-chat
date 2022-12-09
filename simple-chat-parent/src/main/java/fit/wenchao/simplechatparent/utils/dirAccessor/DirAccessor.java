package fit.wenchao.simplechatparent.utils.dirAccessor;

import jnr.posix.FileStat;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import jnr.posix.util.DefaultPOSIXHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static fit.wenchao.simplechatparent.utils.StrUtils.filePathBuilder;
import static fit.wenchao.simplechatparent.utils.StrUtils.ft;
import static jnr.constants.platform.OpenFlags.O_RDONLY;

public class DirAccessor implements IDirAccessor {
    public static final DirAccessor SINGLETON = new DirAccessor();
    private final POSIX posix = POSIXFactory.getPOSIX(new DefaultPOSIXHandler(), true);


    //public File processTarget(File nowFile, String target) {
    //
    //}
    @Override
    public void cd(String target) throws IOException {
        File targetFile;
        File nowFile;
        String now = System.getProperty("user.dir");
        //
        //if(target.equals("..")){
        //    nowFile = new File(now);
        //    target = nowFile.getParentFile().getAbsolutePath();
        //
        //} else if(target.equals(".")) {
        //
        //}

        if (target.startsWith("/")) {
            targetFile = new File(target);
            if (!targetFile.exists()) {
                throw new FileNotFoundException(ft("File Not Found: {}", targetFile));
            }
            posix.chdir(target);
            System.setProperty("user.dir", targetFile.getCanonicalFile().getAbsolutePath());
        } else {

            targetFile = new File(filePathBuilder().ct(now).ct(target).build());
            if (!targetFile.exists()) {
                throw new FileNotFoundException(ft("File Not Found: {}", targetFile));
            }

            String absolutePath = targetFile.getCanonicalFile().getAbsolutePath();
            posix.chdir(absolutePath);
            System.setProperty("user.dir", absolutePath);
        }
    }


    @Override
    public String pwd() {
        String nowDir = System.getProperty("user.dir");
        System.out.println(nowDir);
        return nowDir;
    }

    private  void lss(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                int fd = posix.open(f.getAbsolutePath(), O_RDONLY.intValue(), 0);
                FileStat fstat = posix.fstat(fd);
                
                System.out.println(fstat.mode());
                System.out.println(f);
            }
        } else {
            System.out.println("");
        }
    }


    public List<String> ll(String target) {

        String dirnow = pwd();
        String dirname = target.replace(" ", "");
        if (dirname.equals("")) {
            File dir = new File(dirnow);

            lss(dir);
        } else {
            File dir = new File(dirnow + File.separator + dirname);
            lss(dir);
        }
        return null;
    }

}