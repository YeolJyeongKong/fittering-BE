package fittering.mall.domain.collection;

import fittering.mall.domain.entity.Product;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Products {
    private List<Product> products = new ArrayList<>();

    @Builder
    public Products(List<Product> products) {
        this.products = products;
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public void add(Product product) {
        products.add(product);
    }

    public int size() {
        return products.size();
    }

    public Product get(int index) {
        return products.get(index);
    }

    public List<Long> getProductIds() {
        return products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
    }
}
