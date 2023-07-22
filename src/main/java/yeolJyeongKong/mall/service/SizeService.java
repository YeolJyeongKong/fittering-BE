package yeolJyeongKong.mall.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.BottomDto;
import yeolJyeongKong.mall.domain.dto.TopDto;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.domain.entity.Size;
import yeolJyeongKong.mall.repository.SizeRepository;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;

    public Size saveTop(TopDto topDto) {
        Size size = new Size(topDto);
        sizeRepository.save(size);
        return size;
    }

    public Size saveBottom(BottomDto bottomDto) {
        Size size = new Size(bottomDto);
        sizeRepository.save(new Size(bottomDto));
        return size;
    }

    public void setProduct(Size size, Product product) {
        size.setProduct(product);
    }
}
