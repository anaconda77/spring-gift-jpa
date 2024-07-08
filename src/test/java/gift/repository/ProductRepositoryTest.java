package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import gift.model.Product;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;
    Product product = new Product("productA", 1000, "https://a.com");

    @Test
    @DisplayName("Product insert 테스트")
    void insert() {
        assertThat(product.getId()).isNull();
        Product saveProduct = productRepository.save(product);
        assertThat(saveProduct.getId()).isNotNull();
    }

    @Test
    @DisplayName("Product update 테스트")
    void update() {
        Product savedProduct = productRepository.save(product);

        Product findProduct = productRepository.findById(savedProduct.getId()).get();
        findProduct.setName("productB");
        findProduct.setPrice(10000);
        findProduct.setImageUrl("https://b.com");
        Product modifiedSavedProduct = productRepository.save(findProduct);

        assertThat(modifiedSavedProduct.getId()).isEqualTo(savedProduct.getId());
        assertThat(modifiedSavedProduct.getName()).isEqualTo(findProduct.getName());
        assertThat(modifiedSavedProduct.getPrice()).isEqualTo(findProduct.getPrice());
        assertThat(modifiedSavedProduct.getImageUrl()).isEqualTo(findProduct.getImageUrl());

    }

    @Test
    @DisplayName("Product delete 테스트")
    void delete() {
        Product savedProduct = productRepository.save(product);
        productRepository.delete(savedProduct);

        // 삭제된 상품을 조회할 수 없어야 한다.
        assertThat( productRepository.findById(savedProduct.getId())).isEmpty();
    }

    @Test
    @DisplayName("Product findAll 테스트")
    void findAll() {

        List<Product> products = Stream.generate(
                () -> new Product("product", 1000, "https://a.com"))
            .limit(10)
            .toList();

        products.forEach(
            product -> productRepository.save(product)
        );

        List<Product> findProducts = productRepository.findAll();
        assertThat(findProducts).hasSize(10);

        IntStream.range(0, 10)
            .forEach( i -> {
                Product p = findProducts.get(i);
                assertThat(p.getId()).isNotNull();
                assertThat(p).isEqualTo(products.get(i));
            });

    }

    @Test
    @DisplayName("Product findById 테스트")
    void findById() {
        Product savedProduct = productRepository.save(product);
        Product findProduct = productRepository.findById(savedProduct.getId()).get();

        assertThat(findProduct).isEqualTo(savedProduct);
    }


}