// Copyright (c) 2016 Nikita Shpak aka eqlbin
// 
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// 
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
// CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
// SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package ru.eqlbin.utils.net.ipv4;

/**
 * 
 * A set of static methods for working with subnets. 
 * 
 * @author Nikita Shpak aka eqlbin
 *
 */
public final class IPv4SubnetUtils {
    
    private IPv4SubnetUtils() {}
    
    public static final int MAX_PREFIX_LENGTH = 32;
    public static final int MIN_PREFIX_LENGTH = 1;
    
    /**
     * Converts an integer representation of the subnet mask in
     * the length of the routing prefix in bits (CIDR notation).
     * 
     * @param subnet mask an integer representation of the subnet mask
     * @return the length of the routing prefix in bits
     * @throws InvalidSubnetMaskException
     */
    public static int subnetMaskToPrefixLength(int subnetMask){
        int prefixLength = validateSubnetMask(subnetMask);
        return prefixLength;
    }
    
    
    /**
     * @param subnetMask
     * @return routing prefix length if the given subnet mask is valid
     * @throws InvalidSubnetMaskException
     */
    private static int validateSubnetMask(int subnetMask) {
        int prefixLength = Integer.bitCount(subnetMask);
        if(prefixLength < MIN_PREFIX_LENGTH || prefixLengthToSubnetMask(prefixLength) != subnetMask)
            throw new InvalidSubnetMaskException("'"+IPv4Utils.asString(subnetMask)+"'");
        return prefixLength;
    }
    
    /**
     * Converts the length of the routing prefix in bits (CIDR notation) in 
     * an integer representation of the subnet mask.
     * 
     * @param routingPrefixLength the length of the routing prefix in bits
     * @return an integer representation of the subnet mask
     * @throws InvalidRoutingPrefixLengthException
     */
    public static int prefixLengthToSubnetMask(int routingPrefixLength){
        
        if(routingPrefixLength < MIN_PREFIX_LENGTH || routingPrefixLength > MAX_PREFIX_LENGTH)
            throw new InvalidRoutingPrefixLengthException(
                    "Incorrect routing prefix length: " + routingPrefixLength +
                    ". It must be between " + MIN_PREFIX_LENGTH +" and " + 
                    MAX_PREFIX_LENGTH + " (both inclusive)");
        
        int val = 0;
        int shift = MAX_PREFIX_LENGTH;
        while(shift-- > MAX_PREFIX_LENGTH - routingPrefixLength){
            val = val | 1 << shift;
        }

        return val;
    }    
    
    /**
     * Calculates the count of IP addresses per subnet for the given subnet mask.
     * 
     * @param subnet mask an integer representation of the subnet mask
     * @return the count of IP addresses per subnet
     * @throws InvalidSubnetMaskException
     */
    public static long addressPerSubnetCount(int subnetMask){
       return (long)(Math.pow(2, 32 - subnetMaskToPrefixLength(subnetMask)) - 2);
    }
    
    /**
     * Calculates the count of subnets for the given subnet mask.
     * 
     * @param subnet mask an integer representation of the subnet mask
     * @return the count of subnets
     * @throws InvalidSubnetMaskException
     */
    public static long subnetCount(int subnetMask){
        return (long)(Math.pow(2, subnetMaskToPrefixLength(subnetMask) % 8));
    }
    
    /**
     * Calculates the network (routing prefix) for the given IP
     * address and subnet mask.
     * 
     * @param ip an integer representation of the IP address
     * @param subnet mask an integer representation of the subnet mask
     * @return integer an representation of the network
     * @throws InvalidSubnetMaskException
     */
    public static int network(int ip, int subnetMask){
        validateSubnetMask(subnetMask);
        return ip & subnetMask;
    } 
    
    /**
     * Calculates the broadcast address for given IP address or network and the subnet mask 
     * 
     * @param ip an integer representation of the IP address
     * @param subnet mask an integer representation of the subnet mask
     * @return an integer representation of the broadcast address
     * @throws InvalidSubnetMaskException
     */
    public static int brodcast(int ip, int subnetMask){
        validateSubnetMask(subnetMask);
        return ip | ~subnetMask;
    }    

    /**
     * Checks whether the given IP address in the range of IP addresses 
     * for subnet calculated by given network and subnet mask.
     * Network and broadcast addresses are excluded.
     * 
     * @param ip an integer representation of the IP address
     * @param network an integer representation of the network (routing prefix)
     * @param subnet mask an integer representation of the subnet mask
     * @return true if IP in range and false in otherwise
     * @throws InvalidSubnetMaskException
     */
    public static boolean isInRange(int ip, int network, int subnetMask){
        validateSubnetMask(subnetMask);
        return ip >= network && ip <= brodcast(network, subnetMask);
    }
}
