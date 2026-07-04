# Project

<skills_system priority="1">

## 可用技能

<!-- SKILLS_TABLE_START -->
<usage>
当用户要求执行任务时，检查以下可用技能是否可以更有效地完成任务。技能提供专门的领域知识和能力。

如何使用技能:
- 使用 `file_search` 查找技能文件: `file_search(query="**/skills/<skill-name>/SKILL.md")`
- 使用 `read_file` 从发现的路径加载技能内容
- 按照技能文件中的说明完成任务
- 输出中提供的基础目录用于解析捆绑资源 (references/, scripts/, assets/)

使用须知:
- 仅使用下面 <available_skills> 中列出的技能
- 不要调用已加载到上下文中的技能
- 每次技能调用都是无状态的
</usage>

<available_skills>

<skill>
<name>openspec-archiving</name>
<description>Archives completed changes and merges specification deltas into living documentation. Use when changes are deployed, ready to archive, or specs need updating after implementation. Triggers include "openspec archive", "archive change", "merge specs", "complete proposal", "update documentation", "finalize spec", "mark as done".</description>
</skill>

<skill>
<name>openspec-archiving-cn</name>
<description>归档已完成的变更并将规范差异合并到常驻文档。用于变更已部署、准备归档或实施后需要更新规范时。触发词包括 "openspec归档", "归档", "归档提案", "合并规范", "完成提案", "更新文档", "定稿规范", "标记完成"。</description>
</skill>

<skill>
<name>openspec-context-loading</name>
<description>Loads project context, lists existing specs and changes, searches capabilities and requirements. Use when user asks about project state, existing specs, active changes, available capabilities, or needs context discovery. Triggers include "openspec context", "what specs exist", "show changes", "list capabilities", "project context", "find specs", "what's in the spec", "show me specs".</description>
</skill>

<skill>
<name>openspec-context-loading-cn</name>
<description>加载项目上下文，列出现有规范与变更，搜索能力与需求。用于用户询问项目状态、现有规范、进行中的变更、可用能力或需要发现上下文时。触发词包括"openspec上下文", "有哪些规范", "显示变更", "列出能力", "项目上下文", "查找规范", "规范包含什么", "展示规范"。</description>
</skill>

<skill>
<name>openspec-implementation</name>
<description>Implements approved specification proposals by working through tasks sequentially with testing and validation. Use when implementing changes, applying proposals, executing spec tasks, or building from approved plans. Triggers include "openspec implement", "implement", "apply change", "execute spec", "work through tasks", "build feature", "start implementation".</description>
</skill>

<skill>
<name>openspec-implementation-cn</name>
<description>以测试与验证为先的方式，按序执行并实现已批准的规范提案。用于实施变更、应用提案、执行规范任务或按已批准计划构建。触发词包括 "openspec开发", "开发", "实施" "实现提案", "应用变更", "执行规范", "按顺序完成任务", "构建功能", "开始实施"。</description>
</skill>

<skill>
<name>openspec-proposal-creation</name>
<description>Creates structured change proposals with specification deltas for new features, breaking changes, or architecture updates. Use when planning features, creating proposals, speccing changes, introducing new capabilities, or starting development workflows. Triggers include "openspec proposal", "create proposal", "plan change", "spec feature", "new capability", "add feature planning", "design spec".</description>
</skill>

<skill>
<name>openspec-proposal-creation-cn</name>
<description>通过openspec规范驱动的方法创建结构化的变更提案与规范差异。用于规划功能、创建提案、编写规范、引入新能力或启动开发流程。触发词包括 "openspec提案", "规划", "创建提案", "规划变更", "规范功能", "新功能", "新特性", "新需求", "添加功能规划", "设计规范"。</description>
</skill>

<skill>
<name>speckit-analyze-zh</name>
<description>对spec.md、plan.md和tasks.md三个核心文档进行非破坏性跨工件一致性和质量分析。在任务生成后识别不一致、重复、模糊和规范不足的项目。触发词包括："speckit-analyze"、"speckit分析"、"文档一致性分析"、"规范分析"、"质量检查"、"工件分析"、"spec分析"、"plan分析"、"task分析"。</description>
</skill>

<skill>
<name>speckit-checklist-zh</name>
<description>基于用户需求为当前功能生成定制检查清单的专业工具。专门用于需求质量验证，生成"英语的单元测试"，验证需求的完整性、清晰度和一致性。触发词：speckit-checklist、检查清单、需求验证、质量检查、checklist、requirements validation、质量审查、spec review</description>
</skill>

<skill>
<name>speckit-clarify-zh</name>
<description>通过提出最多5个高度针对性的澄清问题来识别当前功能规范中未明确定义的领域，并将答案编码回规范中。触发词包括："speckit-clarify"、"speckit澄清"、"规范澄清"、"功能澄清"、"识别模糊点"、"澄清需求"。</description>
</skill>

<skill>
<name>speckit-constitution-zh</name>
<description>从交互式或提供的原则输入创建或更新项目章程，确保所有依赖模板保持同步。用于项目管理、规范制定、章程维护和团队协作场景。触发词包括 "speckit章程"、"创建章程"、"更新章程"、"项目章程"、"制定规范"、"团队章程"。</description>
</skill>

<skill>
<name>speckit-implement-zh</name>
<description>通过测试与验证为先的方式，按序执行并实现已批准的规范提案。用于实施变更、应用提案、执行规范任务或按已批准计划构建。触发词包括 "speckit-implement", "speckit开发", "开发", "实施" "实现提案", "应用变更", "执行规范", "按顺序完成任务", "构建功能", "开始实施"。</description>
</skill>

<skill>
<name>speckit-plan-zh</name>
<description>执行实施规划工作流程，使用计划模板生成设计工件。触发词包括："speckit计划"。</description>
</skill>

<skill>
<name>speckit-specify-zh</name>
<description>中文功能规范创建工具，用于将自然语言功能描述转换为结构化的功能规范文档。支持自动生成分支名称、创建Git分支、初始化规范文件和质量验证。触发词包括："speckit规范"、"功能规范"、"创建规范"、"功能描述转换"、"speckit-specify"。当用户需要将功能想法转换为结构化规范时使用此技能。</description>
</skill>

<skill>
<name>speckit-tasks-zh</name>
<description>基于speckit工作流的任务生成技能，用于根据可用设计文档生成可操作的、依赖有序的tasks.md。当需要基于spec.md、plan.md、data-model.md、contracts/等设计文档为功能开发生成详细任务列表时使用此技能。触发词包括"speckit tasks"、"生成任务"、"任务规划"、"功能任务分解"、"创建tasks.md"等。</description>
</skill>

<skill>
<name>ui-ux-pro-max</name>
<description>"UI/UX design intelligence. 67 styles, 96 palettes, 57 font pairings, 25 charts, 13 stacks (React, Next.js, Vue, Svelte, SwiftUI, React Native, Flutter, Tailwind, shadcn/ui). Actions: plan, build, create, design, implement, review, fix, improve, optimize, enhance, refactor, check UI/UX code. Projects: website, landing page, dashboard, admin panel, e-commerce, SaaS, portfolio, blog, mobile app, .html, .tsx, .vue, .svelte. Elements: button, modal, navbar, sidebar, card, table, form, chart. Styles: glassmorphism, claymorphism, minimalism, brutalism, neumorphism, bento grid, dark mode, responsive, skeuomorphism, flat design. Topics: color palette, accessibility, animation, layout, typography, font pairing, spacing, hover, shadow, gradient. Integrations: shadcn/ui MCP for component search and examples."</description>
</skill>

</available_skills>
<!-- SKILLS_TABLE_END -->

</skills_system>
