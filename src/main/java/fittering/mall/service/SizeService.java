package fittering.mall.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fittering.mall.domain.dto.BottomDto;
import fittering.mall.domain.dto.DressDto;
import fittering.mall.domain.dto.OuterDto;
import fittering.mall.domain.dto.TopDto;
import fittering.mall.domain.entity.*;
import fittering.mall.repository.*;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;
    private final TopRepository topRepository;
    private final DressRepository dressRepository;
    private final BottomRepository bottomRepository;
    private final OuterRepository outerRepository;

    @Transactional
    public Size saveOuter(OuterDto outerDto, Product product) {
        Outer outer = outerRepository.save(Outer.builder()
                                            .full(outerDto.getFull())
                                            .shoulder(outerDto.getShoulder())
                                            .chest(outerDto.getChest())
                                            .sleeve(outerDto.getSleeve())
                                            .build());
        return sizeRepository.save(Size.builder()
                                    .name(outerDto.getName())
                                    .outer(outer)
                                    .product(product)
                                    .build());
    }

    @Transactional
    public Size saveTop(TopDto topDto, Product product) {
        Top top = topRepository.save(Top.builder()
                                        .full(topDto.getFull())
                                        .shoulder(topDto.getShoulder())
                                        .chest(topDto.getChest())
                                        .sleeve(topDto.getSleeve())
                                        .build());
        return sizeRepository.save(Size.builder()
                                    .name(topDto.getName())
                                    .top(top)
                                    .product(product)
                                    .build());
    }

    @Transactional
    public Size saveDress(DressDto dressDto, Product product) {
        Dress dress = dressRepository.save(Dress.builder()
                                            .full(dressDto.getFull())
                                            .shoulder(dressDto.getShoulder())
                                            .chest(dressDto.getChest())
                                            .waist(dressDto.getWaist())
                                            .armHall(dressDto.getArmHall())
                                            .hip(dressDto.getHip())
                                            .sleeve(dressDto.getSleeve())
                                            .sleeveWidth(dressDto.getSleeveWidth())
                                            .bottomWidth(dressDto.getBottomWidth())
                                            .build());
        return sizeRepository.save(Size.builder()
                                    .name(dressDto.getName())
                                    .dress(dress)
                                    .product(product)
                                    .build());
    }

    @Transactional
    public Size saveBottom(BottomDto bottomDto, Product product) {
        Bottom bottom = bottomRepository.save(Bottom.builder()
                                                .full(bottomDto.getFull())
                                                .waist(bottomDto.getWaist())
                                                .thigh(bottomDto.getThigh())
                                                .rise(bottomDto.getRise())
                                                .bottomWidth(bottomDto.getBottomWidth())
                                                .hipWidth(bottomDto.getHipWidth())
                                                .build());
        return sizeRepository.save(Size.builder()
                                    .name(bottomDto.getName())
                                    .bottom(bottom)
                                    .product(product)
                                    .build());
    }
}
