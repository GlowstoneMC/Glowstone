/**
 * Copyright (c) 2013, agaricus. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * The name of the author may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipeList;

import java.util.HashMap;
import java.util.Random;

public class EntityVillager extends Entity {

    /** emerald buy recipes */
    public static HashMap<Integer, Tuple> field_70958_bB = new HashMap<Integer, Tuple>();

    /** emerald sell recipes */
    public static HashMap<Integer, Tuple> field_70960_bC = new HashMap<Integer, Tuple>();

    /** add emerald buy recipe */
    public void func_70948_a(MerchantRecipeList merchantRecipeList, int x, Random random, float chance) {

    }

    /** add emerald sell recipe */
    public void func_70949_b(MerchantRecipeList merchantRecipeList, int x, Random random, float chance) {

    }

    /** apply random trade */
    public void func_70938_b(int n) {

    }

    public void setProfession(int profession) {

    }

    public interface ITradeList {

    }

    public static class EmeraldForItems implements ITradeList {

        public EmeraldForItems(Item item, PriceInfo priceInfo) {

        }
    }

    public static class ItemAndEmeraldToItem implements ITradeList {

        public ItemAndEmeraldToItem(Item item1, PriceInfo priceInfo1, Item item2, PriceInfo priceInfo2) {

        }
    }

    public static class ListEnchantedBookForEmeralds implements ITradeList {
    }

    public static class ListEnchantedItemForEmeralds implements ITradeList {

        public ListEnchantedItemForEmeralds(Item item, PriceInfo priceInfo) {

        }
    }

    public static class ListItemForEmeralds implements ITradeList {

        public ListItemForEmeralds(Item item, PriceInfo priceInfo) {

        }

        public ListItemForEmeralds(ItemStack itemStack, PriceInfo priceInfo) {

        }
    }

    public static class PriceInfo {

        public PriceInfo(int a, int b) {

        }
    }
}
