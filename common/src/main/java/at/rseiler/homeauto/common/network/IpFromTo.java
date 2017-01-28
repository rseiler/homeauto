package at.rseiler.homeauto.common.network;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Takes two IPs and generates, one by one, all IPs between them. The class implements Iterable so it's easy to iterate
 * over all IPs.
 */
public class IpFromTo implements Iterable<String> {
    private final int[] from;
    private final int[] to;

    public IpFromTo(String from, String to) {
        this.from = mapToInt(from);
        this.to = mapToInt(to);
    }

    @Override
    public Iterator<String> iterator() {
        return new IpFromToIterator(from, to);
    }

    /**
     * Iterator for the IpFromTo class.
     */
    private static final class IpFromToIterator implements Iterator<String> {
        private final int[] from;
        private final int[] to;
        private final int[] current;
        private boolean first = true;

        private IpFromToIterator(int[] from, int[] to) {
            this.from = Arrays.copyOf(from, from.length);
            this.current = Arrays.copyOf(from, from.length);
            this.to = Arrays.copyOf(to, to.length);
        }

        @Override
        public boolean hasNext() {
            if(first) {
                return true;
            }

            for (int index = 0; index < 4; index++) {
                if (current[index] < to[index]) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public String next() {
            if(first) {
                first = false;
                return ip();
            }

            for (int i = 3; i >= 0; i--) {
                if (current[i] < to[i]) {
                    current[i]++;
                    return ip();
                } else {
                    current[i] = from[i];
                }
            }

            throw new NoSuchElementException();
        }

        private String ip() {
            return current[0] + "." + current[1] + "." + current[2] + "." + current[3];
        }
    }

    private static int[] mapToInt(String from) {
        return Arrays.stream(from.split("\\.")).mapToInt(Integer::parseInt).toArray();
    }
}
