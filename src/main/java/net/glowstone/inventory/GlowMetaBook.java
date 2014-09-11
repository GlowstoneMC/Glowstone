package net.glowstone.inventory;

import com.google.common.collect.ImmutableList;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The ItemMeta for book and quill and written book items.
 */
class GlowMetaBook extends GlowMetaItem implements BookMeta {

    private String title;
    private String author;
    private List<String> pages;

    public GlowMetaBook(GlowMetaItem meta) {
        super(meta);
        if (meta == null || !(meta instanceof GlowMetaBook)) {
            return;
        }
        GlowMetaBook book = (GlowMetaBook) meta;
        title = book.title;
        author = book.author;
        if (book.hasPages()) {
            pages = new ArrayList<>(book.pages);
            filterPages();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internal stuff

    @Override
    public BookMeta clone() {
        return new GlowMetaBook(this);
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.BOOK_AND_QUILL || material == Material.WRITTEN_BOOK;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "BOOK");
        if (hasAuthor()) {
            result.put("author", author);
        }
        if (hasTitle()) {
            result.put("title", title);
        }
        if (hasPages()) {
            result.put("pages", pages);
        }
        return result;
    }

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);
        if (hasAuthor()) {
            tag.putString("author", author);
        }
        if (hasTitle()) {
            tag.putString("title", title);
        }
        if (hasPages()) {
            tag.putList("pages", TagType.STRING, pages);
        }
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if (tag.isString("author")) {
            author = tag.getString("author");
        }
        if (tag.isString("title")) {
            title = tag.getString("title");
        }
        if (tag.isList("pages", TagType.STRING)) {
            pages = tag.getList("pages", TagType.STRING);
            filterPages();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    @Override
    public boolean hasTitle() {
        return title != null && !title.isEmpty();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean setTitle(String title) {
        if (title != null && title.length() > 16) {
            title = title.substring(0, 16);
        }
        this.title = title;
        return true;
    }

    @Override
    public boolean hasAuthor() {
        return author != null && !author.isEmpty();
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean hasPages() {
        return pages != null && !pages.isEmpty();
    }

    @Override
    public String getPage(int page) {
        return pages.get(page);
    }

    @Override
    public void setPage(int page, String data) {
        int size = getPageCount();
        if (page < 0 || page > size) {
            throw new IndexOutOfBoundsException("cannot set page " + page + " on size " + size);
        }
        if (!hasPages()) {
            pages = new ArrayList<>();
        }
        if (page == size) {
            pages.add(data);
        } else {
            pages.set(page, data);
        }
        filterPages();
    }

    @Override
    public List<String> getPages() {
        return ImmutableList.copyOf(pages);
    }

    @Override
    public void setPages(List<String> pages) {
        this.pages = new ArrayList<>(pages);
        filterPages();
    }

    @Override
    public void setPages(String... pages) {
        this.pages = new ArrayList<>(Arrays.asList(pages));
        filterPages();
    }

    @Override
    public void addPage(String... pages) {
        if (!hasPages()) {
            this.pages = new ArrayList<>();
        }
        this.pages.addAll(Arrays.asList(pages));
        filterPages();
    }

    @Override
    public int getPageCount() {
        return hasPages() ? pages.size() : 0;
    }

    /**
     * Filter the page list to 50 pages and 256 characters per page.
     */
    private void filterPages() {
        while (pages.size() > 50) {
            pages.remove(50);
        }
        for (int i = 0; i < pages.size(); ++i) {
            String page = pages.get(i);
            if (page.length() > 256) {
                pages.set(i, page.substring(0, 256));
            }
        }
    }
}
