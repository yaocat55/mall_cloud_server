package cn.net.mall.recommend.support;

import cn.net.mall.product.dto.ProductViewRecordDTO;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.*;

public class RecommendMahoutHelper {

    public static DataModel buildDataModel(List<ProductViewRecordDTO> records) {
        Map<Long, List<GenericPreference>> map = new HashMap<>();
        for (ProductViewRecordDTO r : records) {
            if (r.getUserId() == null || r.getProductId() == null) {
                continue;
            }
            List<GenericPreference> prefs = map.computeIfAbsent(r.getUserId(), k -> new ArrayList<>());
            prefs.add(new GenericPreference(r.getUserId(), r.getProductId(), Math.max(1, r.getViewCount())));
        }
        FastByIDMap<PreferenceArray> data = new FastByIDMap<>();
        for (Map.Entry<Long, List<GenericPreference>> e : map.entrySet()) {
            GenericUserPreferenceArray arr = new GenericUserPreferenceArray(e.getValue());
            data.put(e.getKey(), arr);
        }
        return new GenericDataModel(data);
    }

    public static List<Long> recommendByUser(DataModel model, long userId, int topN, int neighborSize) throws Exception {
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(neighborSize, similarity, model);
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List<RecommendedItem> items = recommender.recommend(userId, topN);
        List<Long> result = new ArrayList<>();
        for (RecommendedItem item : items) {
            result.add(item.getItemID());
        }
        return result;
    }

    public static List<Long> recommendByItem(DataModel model, long itemId, int topN) throws Exception {
        ItemSimilarity similarity = new LogLikelihoodSimilarity(model);
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model, similarity);
        List<RecommendedItem> items = recommender.mostSimilarItems(itemId, topN);
        List<Long> result = new ArrayList<>();
        for (RecommendedItem item : items) {
            result.add(item.getItemID());
        }
        return result;
    }
}
