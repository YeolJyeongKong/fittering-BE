package yeolJyeongKong.mall.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.BottomDto;
import yeolJyeongKong.mall.domain.dto.TopDto;
import yeolJyeongKong.mall.domain.entity.Bottom;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.domain.entity.Size;
import yeolJyeongKong.mall.domain.entity.Top;
import yeolJyeongKong.mall.repository.BottomRepository;
import yeolJyeongKong.mall.repository.SizeRepository;
import yeolJyeongKong.mall.repository.TopRepository;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;
    private final TopRepository topRepository;
    private final BottomRepository bottomRepository;

    public Size saveTop(TopDto topDto, Product product) {
        Top top = topRepository.save(new Top(topDto));
        return sizeRepository.save(new Size(topDto.getName(), top, product));
    }

    public Size saveBottom(BottomDto bottomDto, Product product) {
        Bottom bottom = bottomRepository.save(new Bottom(bottomDto));
        return sizeRepository.save(new Size(bottomDto.getName(), bottom, product));
    }

    public void setProduct(Size size, Product product) {
        size.setProduct(product);
    }
}
