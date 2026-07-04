export const meta = {
  name: 'bff-swagger-complete',
  description: '补齐 BFF 所有缺失的 Swagger 文档接口',
  phases: [
    { title: 'Product CRUD', detail: '商品 + 分组 + 属性 + 轮播 + 公告 + 推荐商品' },
    { title: 'Basic CRUD', detail: '定时任务 + 通知 + 图片 + 敏感词 + 短信 + 字典详情' },
    { title: 'Order & Marketing 补全', detail: '订单收货地址 + 发券/领券/秒杀记录' },
  ],
}

// Helper: read frontend API files to know exact URL patterns
function collectFrontendUrls() {
  const fs = require('fs')
  const path = require('path')
  const apiDir = path.join(args.webSrc, 'api')
  const result = {}
  const dirs = fs.readdirSync(apiDir)
  for (const d of dirs) {
    const dirPath = path.join(apiDir, d)
    if (!fs.statSync(dirPath).isDirectory()) continue
    const files = fs.readdirSync(dirPath).filter(f => f.endsWith('.js'))
    for (const f of files) {
      const content = fs.readFileSync(path.join(dirPath, f), 'utf8')
      const urls = [...content.matchAll(/url:\s*['"]([^'"]+)['"]/g)].map(m => m[1])
      if (urls.length) result[`${d}/${f}`] = urls
    }
  }
  return result
}

const urls = collectFrontendUrls()

// Agent 1: Product CRUD endpoints
phase('Product CRUD')
const productResult = await agent({
  prompt: `Read the frontend API files at ${args.webSrc}/api/product/ to get all URL patterns.
Then for each entity (product, productGroup, attribute, attributeValue, indexCarouselImage, indexNotice, indexProduct):

1. **Feign Client** - Create or extend a Feign client in ${args.productClient}/ that maps to the correct backend controller URLs. Backend controllers are at /v1/{entity} with standard CRUD (insert, update, deleteByIds, searchByPage, findById).

2. **BFF Controller** - Create a file ${args.adminController}/AdminProductExtraController.java following this exact pattern:
   \`\`\`java
   @RestController
   @RequestMapping("/admin/v1/product-extra")
   @Tag(name = "管理后台-商品扩展数据", description = "商品分组、属性、属性值、首页配置等")
   public class AdminProductExtraController {
       // Each method: @Operation + @PostMapping + delegate to Feign client
   }
   \`\`\`

For the Product entity itself, THE FEIGN CLIENT ALREADY EXISTS at ${args.productClient}/ProductFeignClient.java - just add the missing insert/update/deleteByIds/searchByPage/findById methods to it.

For the ProductController BFF, UPDATE ${args.adminController}/AdminProductController.java to add save/update/delete/page/detail endpoints alongside the existing getProductEditData.

IMPORTANT: Every method must have @Operation with summary. Use the exact URLs from the frontend as reference. The frontend calls patterns like:
- /api/product/v1/product/searchByPage → BFF maps to /admin/v1/product/page
- /api/product/v1/product/insert → BFF maps to /admin/v1/product/save

Write your report to a file and return DONE with file path.`,
  schema: { type: 'object', properties: { result: { type: 'string' }, files: { type: 'array', items: { type: 'string' } } }, required: ['result', 'files'] },
  phase: 'Product CRUD',
  effort: 'high',
})

// Agent 2: Basic service CRUD endpoints  
phase('Basic CRUD')
const basicResult = await agent({
  prompt: `Read the frontend API files at ${args.webSrc}/api/common/ and collect the URL patterns.
Then for each missing entity that has URL patterns like api/basic/v1/{entity}/{action}:

Create Feign client interfaces in ${args.basicClient}/ for:
- JobFeignClient.java (commonJob: searchByPage, insert, update, deleteByIds, pause, resume, runNow)
- JobLogFeignClient.java (commonJobLog: searchByPage, insert, deleteByIds, update)
- NotifyFeignClient.java (commonNotify: searchByPage, insert, deleteByIds, update)
- PhotoFeignClient.java (commonPhoto: searchByPage, insert, deleteByIds, update)
- PhotoGroupFeignClient.java (commonPhotoGroup: searchByPage, insert, deleteByIds, update)
- SensitiveWordFeignClient.java (commonSensitiveWord: searchByPage, insert, deleteByIds, update)
- SmsRecordFeignClient.java (commonSmsRecord: searchByPage, insert, deleteByIds, update)
- DictDetailFeignClient.java (dictDetail: searchByPage, searchDictDetail, insert, deleteByIds, update)

Backend controllers are in ${args.bffSrc}/mall-basic/ with @RequestMapping like:
- /v1/commonJob → CRUD
- /v1/commonJobLog → CRUD
- /v1/commonNotify → CRUD (but this is actually in mall-message service!)
... etc. Read each actual controller to get the exact URL paths.

Then create ${args.adminController}/AdminBasicController.java with @Tag and @Operation for each.

Write report to file and return DONE.`,
  schema: { type: 'object', properties: { result: { type: 'string' }, files: { type: 'array', items: { type: 'string' } } }, required: ['result', 'files'] },
  phase: 'Basic CRUD',
  effort: 'high',
})

// Agent 3: Order & Marketing existing controllers expansion
phase('Order & Marketing 补全')
const orderResult = await agent({
  prompt: `I need you to expand existing BFF controllers with missing Swagger endpoints.

1. Read the frontend API files:
   - ${args.webSrc}/api/order/trade.js
   - ${args.webSrc}/api/marketing/ (all JS files)
   - ${args.webSrc}/api/shopping/ (all JS files)

2. Update ${args.adminController}/AdminOrderController.java - Add endpoints for:
   - tradeDeliveryAddress (searchByPage, insert, update, deleteByIds)
   - refud (searchByPage, insert, update, deleteByIds)
   - trade shippedByIds
   
   The existing BFF controller uses ${args.orderClient}/OrderFeignClient.java - if methods are missing, add them there.
   
3. Update ${args.adminController}/AdminMarketingController.java - Add endpoints for:
   - couponUserProvide (searchByPage, insert, update, deleteByIds) 
   - couponUserReceive (searchByPage, insert, update, deleteByIds)
   - seckillProduct (searchByPage, insert, update, deleteByIds, findById)
   
   The controller uses ${args.marketingClient}/MarketingFeignClient.java - add methods there.

4. Create ${args.adminController}/AdminShoppingController.java for:
   - deliveryAddress CRUD (uses auth service /v1/mobile/deliveryAddress)
   - productComment/searchByPage, insert, deleteByIds, update
   - productFavorites/searchByPage, insert, deleteByIds, update
   - productViewRecord/searchByPage, insert, deleteByIds, update
   - shoppingCart/searchByPage, insert, deleteByIds, update

   Some Feign clients already exist:
   - ${args.authClient}/DeliveryAddressFeignClient.java
   - ${args.productClient}/ProductFeignClient.java (has some methods)
   Create new Feign clients where none exist (CommentFeignClient, FavoritesFeignClient, ViewRecordFeignClient, CartFeignClient in mall-product-client).

Write report and return DONE.`,
  schema: { type: 'object', properties: { result: { type: 'string' }, files: { type: 'array', items: { type: 'string' } } }, required: ['result', 'files'] },
  phase: 'Order & Marketing 补全',
  effort: 'high',
})

return { product: productResult, basic: basicResult, order: orderResult }
