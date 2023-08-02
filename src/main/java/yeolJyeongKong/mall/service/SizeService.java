package yeolJyeongKong.mall.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.BottomDto;
import yeolJyeongKong.mall.domain.dto.OuterDto;
import yeolJyeongKong.mall.domain.dto.TopDto;
import yeolJyeongKong.mall.domain.entity.*;
import yeolJyeongKong.mall.repository.BottomRepository;
import yeolJyeongKong.mall.repository.OuterRepository;
import yeolJyeongKong.mall.repository.SizeRepository;
import yeolJyeongKong.mall.repository.TopRepository;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;
    private final TopRepository topRepository;
    private final BottomRepository bottomRepository;
    private final OuterRepository outerRepository;

    @Transactional
    public Size saveOuter(OuterDto outerDto, Product product) {
        Outer outer = outerRepository.save(new Outer(outerDto));
        return sizeRepository.save(new Size(outerDto.getName(), outer, product));
    }

    @Transactional
    public Size saveTop(TopDto topDto, Product product) {
        Top top = topRepository.save(new Top(topDto));
        return sizeRepository.save(new Size(topDto.getName(), top, product));
    }

    @Transactional
    public Size saveBottom(BottomDto bottomDto, Product product) {
        Bottom bottom = bottomRepository.save(new Bottom(bottomDto));
        return sizeRepository.save(new Size(bottomDto.getName(), bottom, product));
    }

    @Transactional
    public void setProduct(Size size, Product product) {
        size.setProduct(product);
    }
}
