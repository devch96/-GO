package com.hansung.capstone.course;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import com.hansung.capstone.community.PostDTO;
import com.hansung.capstone.community.PostServiceImpl;
import com.hansung.capstone.user.User;
import com.hansung.capstone.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCourseServiceImpl implements UserCourseService {

    private final UserCourseRepository userCourseRepository;

    private final UserRepository userRepository;

    private final PostServiceImpl postService;

    private final CourseImageInfoRepository courseImageInfoRepository;

    @Override
    public PostDTO.FreePostResponseDTO createCourse(UserCourseDTO.CreateRequestDTO req, List<MultipartFile> files, MultipartFile thumbnail) throws Exception {
        if (req.getCategory().equals("COURSE")) {
            UserCourse newUserCourse = UserCourse.builder()
                    .coordinates(req.getCoordinates())
                    .region(req.getRegion())
                    .post(this.postService.createCourseBoardPost(req,files,thumbnail))
                    .originToDestination(req.getOriginToDestination())
                    .build();
            this.userCourseRepository.save(newUserCourse).getPost();
        }
        return null;
    }

    @Override
    public Page<UserCourse> getCourseListByRegion(int page, String region) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<UserCourse> paging = this.userCourseRepository.findAllByRegion(pageable,region);
        return paging;
    }

    @Override
    public UserCourseDTO.CourseResponseDTO getCourseDetail(Long courseId) {
        return createResponse(this.userCourseRepository.findById(courseId).orElseThrow(
                () -> new RuntimeException("코스가 존재하지 않습니다.")
        ));
    }

    @Transactional
    @Override
    public void setCourseScrap(Long userId, Long courseId) {
        UserCourse userCourse = this.userCourseRepository.findById(courseId).orElseThrow(
                () -> new RuntimeException("코스가 존재하지 않습니다.")
        );

        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("유저가 존재하지 않습니다.")
        );

        if (userCourse.getScraper().contains(user)){
            userCourse.getScraper().remove(user);
        } else{
            userCourse.getScraper().add(user);
        }
    }

    @Override
    public Page<UserCourse> getCourseByScraper(int page, Long userId) {
        return this.userCourseRepository.findAllScrap(userId,this.postService.sortBy(page,"created_date"));
    }

    public UserCourseDTO.CourseResponseDTO createResponse(UserCourse userCourse) {
        List<Long> imageIdList = new ArrayList<>();
        for (int i = 1; i < userCourse.getPost().getPostImages().size(); i++){
            imageIdList.add(userCourse.getPost().getPostImages().get(i).getId());
        }
        List<ImageInfo> imageInfoList = new ArrayList<>();
        for (Long imageId : imageIdList){
            CourseImageInfo courseImageInfo = this.courseImageInfoRepository.findByPostImageId(imageId).orElseThrow(
                    () -> new RuntimeException("정보가 존재하지 않습니다")
            );
            ImageInfo imageInfo = ImageInfo.builder()
                    .coordinate(courseImageInfo.getCoordinate())
                    .placeLink(courseImageInfo.getPlaceLink())
                    .placeName(courseImageInfo.getPlaceName()).build();
            imageInfoList.add(imageInfo);
        }

        UserCourseDTO.CourseResponseDTO res = UserCourseDTO.CourseResponseDTO.builder()
                .courseId(userCourse.getId())
                .coordinates(userCourse.getCoordinates())
                .originToDestination(userCourse.getOriginToDestination())
                .numOfFavorite(userCourse.getPost().getVoter().size())
                .imageId(imageIdList)
                .imageInfoList(imageInfoList)
                .thumbnailId(userCourse.getPost().getPostImages().get(0).getId())
                .region(userCourse.getRegion())
                .postId(userCourse.getPost().getId())
                .build();
        return res;
    }


    private String convertCoordinatesToPolyline(List<List<Double>> req){
        StringBuffer sb = new StringBuffer();
        List<LatLng> latLngList = new ArrayList<>();
        for(List<Double> coordinates : req){
            Double lat = coordinates.get(0);
            Double lng = coordinates.get(1);
            LatLng latLng = new LatLng(lat, lng);
            latLngList.add(latLng);
        }
        sb.append(Polyline.encode(latLngList));
        return sb.toString();
    }

    public static class Polyline extends PolylineEncoding {
        public static List<LatLng> decode(final String encodedPath) {

            int len = encodedPath.length();

            final List<LatLng> path = new ArrayList<>(len / 2);
            int index = 0;
            int lat = 0;
            int lng = 0;

            while (index < len) {
                int result = 1;
                int shift = 0;
                int b;
                do {
                    b = encodedPath.charAt(index++) - 63 - 1;
                    result += b << shift;
                    shift += 5;
                } while (b >= 0x1f);
                lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

                result = 1;
                shift = 0;
                do {
                    b = encodedPath.charAt(index++) - 63 - 1;
                    result += b << shift;
                    shift += 5;
                } while (b >= 0x1f);
                lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

                path.add(new LatLng(lat * 1e-6, lng * 1e-6));
            }

            return path;
        }
        public static String encode(final List<LatLng> path) {
            long lastLat = 0;
            long lastLng = 0;

            final StringBuilder result = new StringBuilder();

            for (final LatLng point : path) {
                long lat = Math.round(point.lat * 1e6);
                long lng = Math.round(point.lng * 1e6);

                long dLat = lat - lastLat;
                long dLng = lng - lastLng;

                encode(dLat, result);
                encode(dLng, result);

                lastLat = lat;
                lastLng = lng;
            }
            return result.toString();
        }

        private static void encode(long v, StringBuilder result) {
            v = v < 0 ? ~(v << 1) : v << 1;
            while (v >= 0x20) {
                result.append(Character.toChars((int) ((0x20 | (v & 0x1f)) + 63)));
                v >>= 5;
            }
            result.append(Character.toChars((int) (v + 63)));
        }
    }
}
