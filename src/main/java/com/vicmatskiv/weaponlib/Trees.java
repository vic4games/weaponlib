package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;

public class Trees {



    /**
     * Array structure.
     *
     * Each array element has either 0 or N children, where N = number of categories,
     *
     * {I1, I2, ..., In}
     *
     * Every node in array is represented by parent index number and sequence of I-s and end of row marker
     *
     * For example:
     *
     *         N1         - row #0
     *     N2     N3      - row #1
     *  N5   N6           - row #2
     *         N7         - row #3
     *
     *  Result:  N1E  0N2 0N3 E 0N5 0N6 E 1N7 E
     *
     *  Entry format: P I  (parent, indexes, end of row)
     *
     *  Row format: PI PI PI ... E
     *
     * Algorithm is based on BFS
     *
     * queue entry = {
     *   parent index:
     *   node:
     * }
     *
     * while queue is not empty
     *   read entry from the queue
     *
     *   if entry is a sentinel
     *     reset counter
     *     continue
     *
     *   end if
     *
     *
     *   add entry.parentIndex to the output array
     *   add entry.node content to the output array
     *
     *   for each child in node.children
     *     add entry to the queue
     *     counter++
     *   end
     *
     *   add end of row sentinel entry
     * end
     *
     */

    private static class Entry<N> {
        N content;
        int index;
        int parentIndex;

        private String name;
        public String toString() {
            return name != null ? name : content.toString();
        }

    };

    public static <N> void writeBuf(ByteBuf buf, N root, BiConsumer<ByteBuf, N> writeContent, Function<N, List<N>> getChildren) {

        Deque<Entry<N>> queue = new LinkedList<>();

        Entry<N> SENTINEL = new Entry<>();
        SENTINEL.name = "Sentinel";

        Entry<N> rootEntry = new Entry<>();
        rootEntry.content = root;
        rootEntry.parentIndex = 0;

        queue.add(rootEntry);
        queue.add(SENTINEL);

        int counter = 0;
        while(!queue.isEmpty()) {
            Entry<N> e = queue.pollFirst();
            if(e == SENTINEL) {
                counter = 0;
                buf.writeByte(-1);
                continue;
            }

            buf.writeInt(e.parentIndex); // where do we get parent index from?
            writeContent.accept(buf, e.content);

            List<N> children = getChildren.apply(e.content);
            if(!children.isEmpty()) {
                for(N n: children) {
                    Entry<N> c = new Entry<>();
                    c.content = n;
                    c.index = counter++;
                    c.parentIndex = e.index;
                    queue.addLast(c);
                }
                queue.addLast(SENTINEL);
            }
        }
        buf.writeByte(-1);
    }

    public static <N> N readBuf(ByteBuf buf, Function<ByteBuf, N> reader, BiConsumer<N, N> attacher) {
        /*
         * Read byte
         *
         * Maintain state?
         *
         *
         *
         *
         *   list previousRow
         *   list currentRow
         *
         *   mark buf
         *   read byte b0
         *   if b0 == sentinel (e.g. -1)
         *     if currentRow is empty
         *       // double sentinel marks eof
         *       break;
         *     end if;
         *     start next row
         *     previousRow = currentRow
         *     currentRow.clear();
         *   else
         *     buf.resetReaderIndex() // unread b0 - it's not a sentinel, but a part of the parent index
         *     parentIndex = buf.readInt();
         *
         *     n = new Node();
         *     parentNode = previousRow.get(parentIndex);
         *     parentNode.addChild(n);
         *     read node content from the buffer
         *     currentRow.add(node);
         *   end if
         *
         *   when do we stop reading? need eof marker
         *
         *
         */

        List<N> currentRow = new ArrayList<>();
        List<N> previousRow = null;

        N root = null;

        while(true) {
            buf.markReaderIndex();
            if(buf.readByte() == -1) {
                if(currentRow.isEmpty()) {
                    break;
                }
                previousRow = currentRow;
                currentRow = new ArrayList<>();
            } else {
                buf.resetReaderIndex();
                int parentIndex = buf.readInt();
                N thisNode = reader.apply(buf);
                if(previousRow != null) {
                    N parentNode = previousRow.get(parentIndex);
                    attacher.accept(parentNode, thisNode);
                } else {
                    root = thisNode;
                }

                currentRow.add(thisNode);
            }
        }

        return root;

    }

}
