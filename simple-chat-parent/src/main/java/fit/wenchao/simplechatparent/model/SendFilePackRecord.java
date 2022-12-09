package fit.wenchao.simplechatparent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendFilePackRecord
{

    String uuid;

    double rate;

    long sentBytes;

    /**
     * Each element indicates whether the corresponding file fragment
     * is received by Client B.
     * The size of the array depends on the number file fragments.
     */
    boolean[] finished;

    /**
     * The number of file fragments which have been confirmed to be received by Client B.
     */
    int sentSuccessSegs;

    public synchronized int getSentSuccessSegs()
    {
        return sentSuccessSegs;
    }

    public void setFinished(boolean[] finished)
    {
        this.finished = finished;
    }

    /**
     * Mark the file with serial number idx has been transferred.
     */
    public void finish(int idx)
    {
        if (!this.finished[idx])
        {
            this.finished[idx] = true;
            this.sentSuccessSegs++;
        }
    }

    /**
     * Indicate whether the transfer is over.
     */
    public boolean finished()
    {
        return sentSuccessSegs == finished.length;
    }
}