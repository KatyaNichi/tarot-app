package com.superapp.luneartarot.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "card_of_day")

data class TarotCard(
    val id: Int,
    val name: String,
    val subcategory: String,
    val uprightDescription: String,
    val reversedDescription: String,
    val uprightMeanings: List<String>,
    val reversedMeanings: List<String>,
    val imageName: String,
    val element: String,
    val answer: String
)
data class CardOfDay(
    val card: TarotCard,
    val isUpright: Boolean
)

class CardRepository(private val context: Context) {
    private val cardOfDayKey = stringPreferencesKey("card_of_day")
    private val cardOrientationKey = stringPreferencesKey("card_orientation")
    private val dateKey = stringPreferencesKey("date")

    private val cards = listOf(
        TarotCard(1, "Queen of Pentacles", "Minor arcana", uprightDescription = "When upright, the Queen of Pentacles represents a nurturing, practical, and abundant energy. She embodies the perfect blend of material success and emotional fulfillment. This card suggests that you are creating a harmonious balance between your work, home life, and personal interests. You have a knack for managing resources wisely and creating a warm, welcoming environment for yourself and others. The Queen encourages you to trust your practical instincts and to share your abundance generously, while maintaining healthy boundaries.",
            "In its reversed position, the Queen of Pentacles may indicate an imbalance in your approach to material wealth and nurturing. You might be placing too much emphasis on material possessions at the expense of emotional connections or self-care. Alternatively, you could be neglecting your financial or domestic responsibilities. This reversal can also suggest feeling overwhelmed by the need to care for others, leading to burnout or resentment. It's a call to reassess your priorities and find a healthier balance between giving and receiving, material and emotional needs.",
            listOf("Nurturing", "Practicality", "Financial Security","Domestic Bliss", "Balance", "Generosity", "Connection to Nature"),
            listOf("Lack of self-care", "Financial insecurity", "Materialism", "Resistance to change", "Feeling overwhelmed"),
            "card_queen_of_pentacles","Earth", "yes"),
        TarotCard(
            2, "Page of Pentacles", "Minor arcana", "The Page of Pentacles represents new beginnings in the material realm. It can signal new job opportunities, financial gains, or the start of a practical venture. The Page of Pentacles encourages a focus on education, learning, and skill-building. It speaks to a sense of ambition and desire to manifest dreams into reality, using a methodical and practical approach.",
            reversedDescription = "Reversed, the Page of Pentacles may suggest missed opportunities or a lack of progress. It could represent laziness, procrastination, or being distracted from long-term goals. Alternatively, it could indicate an unwillingness to learn or grow, leading to stagnation in both career and personal development.",
            listOf("Ambition", "Focus", "Manifestation", "Practicality", "Skill-building", "Opportunity"),
            listOf("Procrastination", "Missed opportunities", "Lack of progress", "Unwillingness to learn", "Distraction"),
            "card_page_of_pentacles", "Earth",
            "no"
        ),
        TarotCard(
            3,
            "Knight of Pentacles",
            "Minor arcana",
            "The Knight of Pentacles symbolizes hard work, perseverance, and reliability. This card represents a methodical, patient, and responsible approach to achieving goals. The Knight of Pentacles values routine, structure, and diligence. This card suggests that steady progress and dedication will lead to success, even if the journey is slow. It encourages a disciplined and practical mindset.",
            "When reversed, the Knight of Pentacles can indicate a lack of progress or stagnation. It may suggest overworking to the point of burnout or being overly focused on details, missing the bigger picture. Alternatively, it could imply laziness, a lack of motivation, or reluctance to take necessary risks. The reversal is a call to reassess one's approach and bring more balance to work and life.",
            listOf("Perseverance", "Hard work", "Diligence", "Routine", "Reliability", "Patience", "Steady progress"),
            listOf("Stagnation", "Overworking", "Burnout", "Laziness", "Lack of motivation", "Risk aversion"),
            "card_knights_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            4,
            "King of Pentacles",
            "Minor arcana",
            "The King of Pentacles represents abundance, security, and mastery over the material world. This card signifies success in financial matters, business ventures, and the ability to create and sustain wealth. The King is practical, disciplined, and committed to achieving long-term goals. He embodies stability, generosity, and a deep connection to the physical and material realm.",
            "When reversed, the King of Pentacles can suggest greed, materialism, or an overemphasis on wealth and status. It may indicate a fear of loss or a tendency to become too controlling or conservative in financial matters. Alternatively, it can signify neglect of emotional or spiritual well-being in favor of material gain. The reversal encourages reflection on what truly brings security and fulfillment.",
            listOf("Abundance", "Security", "Stability", "Discipline", "Generosity", "Financial mastery", "Success"),
            listOf("Greed", "Materialism", "Fear of loss", "Control issues", "Neglect of emotions", "Overemphasis on wealth"),
            "card_king_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            5,
            "Ace of Pentacles",
            "Minor arcana",
            "The Ace of Pentacles represents new beginnings in the material realm, such as career opportunities, financial growth, or a new venture. It symbolizes prosperity, stability, and the potential for success. This card encourages you to seize opportunities that can lead to long-term wealth and security. It's a sign of a fresh start, grounded in practicality and effort.",
            "When reversed, the Ace of Pentacles may suggest missed opportunities or delays in achieving material goals. It could indicate poor planning, financial instability, or a lack of resources. This reversal encourages re-evaluating your approach and ensuring that you have the necessary foundations in place before moving forward.",
            listOf("New beginnings", "Prosperity", "Opportunity", "Stability", "Success", "Practicality", "Effort"),
            listOf("Missed opportunities", "Delays", "Financial instability", "Poor planning", "Lack of resources"),
            "card_aces_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            6,
            "Ten of Pentacles",
            "Minor arcana",
            "The Ten of Pentacles signifies long-term success, stability, and legacy. It represents financial security, family wealth, and a stable foundation for future generations. This card indicates a prosperous period where your hard work has paid off, and you can now enjoy the fruits of your labor. It's also a sign of strong family bonds and long-lasting traditions.",
            "When reversed, the Ten of Pentacles can suggest financial difficulties, family conflicts, or instability in long-term plans. It may indicate concerns about wealth or inheritance, or a feeling of being disconnected from family or community. The reversal encourages you to reassess your goals and consider how to build a more sustainable future.",
            listOf("Wealth", "Security", "Legacy", "Family", "Tradition", "Prosperity", "Long-term success"),
            listOf("Financial instability", "Family conflicts", "Instability", "Inheritance issues", "Disconnection"),
            "card_10_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            7,
            "Nine of Pentacles",
            "Minor arcana",
            "The Nine of Pentacles represents self-sufficiency, luxury, and financial independence. This card indicates that your hard work has allowed you to enjoy a life of comfort and abundance. You are in a strong position to enjoy the rewards of your efforts and appreciate the beauty and luxury around you. It's a card of self-reliance and personal accomplishment.",
            "When reversed, the Nine of Pentacles may suggest overindulgence, a loss of independence, or an inability to enjoy the rewards of your efforts. It could indicate financial dependence on others or feeling isolated despite material success. The reversal encourages you to find balance between work, wealth, and personal fulfillment.",
            listOf("Independence", "Luxury", "Self-sufficiency", "Abundance", "Comfort", "Accomplishment", "Gratitude"),
            listOf("Overindulgence", "Dependency", "Isolation", "Financial struggle", "Imbalance"),
            "card_9_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            8,
            "Eight of Pentacles",
            "Minor arcana",
            "The Eight of Pentacles symbolizes hard work, skill development, and dedication. This card suggests that you are fully committed to mastering your craft and improving your abilities. It represents the focus and discipline needed to achieve long-term success. The 8 of Pentacles encourages you to keep honing your skills and to stay dedicated to your goals.",
            "When reversed, the Eight of Pentacles may suggest a lack of focus, poor work ethic, or dissatisfaction with your current progress. It could indicate a reluctance to put in the effort required for success or a feeling of burnout. The reversal is a reminder to stay patient and committed to your growth.",
            listOf("Skill development", "Hard work", "Discipline", "Focus", "Dedication", "Craftsmanship", "Commitment"),
            listOf("Lack of focus", "Poor work ethic", "Burnout", "Dissatisfaction", "Procrastination"),
            "card_8_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            9,
            "Seven of Pentacles",
            "Minor arcana",
            "The Seven of Pentacles represents patience, assessment, and long-term planning. This card suggests that you are in a period of reflection, evaluating the progress you have made and determining whether to continue on your current path or make adjustments. It's a time to assess your efforts and ensure that you are investing your time and energy wisely.",
            "When reversed, the Seven of Pentacles may suggest impatience, frustration, or feeling that your efforts are not yielding results. It could indicate a lack of planning or misguided investments of time or resources. The reversal encourages you to re-evaluate your strategy and consider whether it's time to change course.",
            listOf("Patience", "Assessment", "Planning", "Reflection", "Investment", "Evaluation", "Long-term goals"),
            listOf("Impatience", "Frustration", "Lack of progress", "Poor planning", "Wasted effort"),
            "card_7_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            10,
            "Six of Pentacles",
            "Minor arcana",
            "The Six of Pentacles symbolizes generosity, charity, and the balance between giving and receiving. This card represents the flow of resources, whether material or emotional, between individuals. It suggests that you are in a position to help others or that you are receiving support from someone. The 6 of Pentacles encourages generosity, sharing, and mutual benefit.",
            "When reversed, the Six of Pentacles may suggest imbalance in giving and receiving, or feelings of exploitation or dependency. It could indicate that you are giving too much without receiving in return or that you are relying too heavily on others. The reversal encourages you to re-evaluate the balance in your relationships and ensure fairness and equality.",
            listOf("Generosity", "Charity", "Balance", "Giving", "Receiving", "Support", "Mutual benefit"),
            listOf("Imbalance", "Exploitation", "Dependency", "Inequality", "Resentment"),
            "card_6_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            11,
            "Five of Pentacles",
            "Minor arcana",
            "The Five of Pentacles represents hardship, loss, and financial struggle. This card suggests that you are going through a difficult time, feeling isolated or abandoned, and experiencing financial or emotional insecurity. It is a reminder that challenges can be overcome with perseverance and that help is available if you reach out.",
            "When reversed, the Five of Pentacles may indicate recovery from hardship, renewed hope, or a gradual improvement in financial or emotional well-being. It could suggest that you are finding new ways to overcome obstacles and are starting to regain your sense of stability. The reversal is a positive sign of moving forward from a difficult period.",
            listOf("Hardship", "Loss", "Struggle", "Isolation", "Financial insecurity", "Adversity", "Perseverance"),
            listOf("Recovery", "Hope", "Improvement", "Stability", "Support", "Rebuilding"),
            "card_5_of_pentacles",
            "Earth",
            "no"
        ),
        TarotCard(
            12,
            "Four of Pentacles",
            "Minor arcana",
            "The Four of Pentacles represents control, security, and holding on to resources. This card suggests that you are focused on maintaining stability and protecting what you have earned. While it indicates financial security, it may also suggest a tendency to become overly possessive or resistant to change. The 4 of Pentacles encourages finding a balance between saving and sharing.",
            "When reversed, the Four of Pentacles can indicate an excessive focus on material security, leading to greed, control issues, or fear of loss. It may suggest that you are holding on too tightly to your resources or are resistant to new opportunities. The reversal encourages loosening your grip and trusting in the flow of abundance.",
            listOf("Control", "Security", "Possessiveness", "Stability", "Saving", "Protection", "Caution"),
            listOf("Greed", "Control issues", "Fear of loss", "Inflexibility", "Materialism"),
            "card_4_of_pentacles",
            "Earth",
            "no"
        ),
        TarotCard(
            13,
            "Three of Pentacles",
            "Minor arcana",
            "The Three of Pentacles symbolizes teamwork, collaboration, and skill-building. This card suggests that you are working with others to achieve a common goal, pooling resources, and talents to create something meaningful. It encourages learning from others and contributing your unique skills to the project. It's a reminder that success comes from cooperation and mutual effort.",
            "When reversed, the Three of Pentacles may suggest a lack of teamwork or issues with collaboration. It could indicate disorganization, conflicts in group dynamics, or a lack of recognition for your efforts. The reversal encourages clear communication and a better division of responsibilities to ensure a successful outcome.",
            listOf("Teamwork", "Collaboration", "Skill-building", "Cooperation", "Learning", "Shared goals"),
            listOf("Disorganization", "Conflicts", "Lack of teamwork", "Lack of recognition", "Frustration"),
            "card_3_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            14,
            "Two of Pentacles",
            "Minor arcana",
            "The Two of Pentacles represents balance, adaptability, and juggling priorities. This card suggests that you are managing multiple responsibilities and are working to maintain balance in your life. It encourages flexibility and resourcefulness as you navigate through different tasks. The 2 of Pentacles is a reminder to stay grounded while managing change and to keep a steady rhythm in your actions.",
            "When reversed, the Two of Pentacles may suggest overwhelm, disorganization, or difficulty maintaining balance. It could indicate that you are struggling to manage your responsibilities and feeling stretched too thin. The reversal encourages you to simplify your tasks and focus on regaining control over your priorities.",
            listOf("Balance", "Adaptability", "Resourcefulness", "Flexibility", "Juggling", "Time management", "Change"),
            listOf("Overwhelm", "Disorganization", "Imbalance", "Stress", "Poor time management"),
            "card_2_of_pentacles",
            "Earth",
            "yes"
        ),
        TarotCard(
            15,
            "Two of Wands",
            "Minor arcana",
            "The Two of Wands represents planning, decision-making, and potential for future growth. You are standing at a crossroads, contemplating your next move. It's a time to make plans, set goals, and consider your long-term direction. The card encourages you to be bold in your choices and to step out of your comfort zone for growth.",
            "When reversed, the Two of Wands may suggest indecision, lack of planning, or fear of the unknown. It can indicate that you are hesitant to take action or are struggling to choose between different paths. The reversal encourages you to overcome your fears and make a confident decision.",
            listOf("Planning", "Decision-making", "Potential", "Growth", "Bold choices", "Expansion"),
            listOf("Indecision", "Lack of planning", "Fear", "Hesitation", "Uncertainty"),
            "card_2_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            16,
            "Three of Wands",
            "Minor arcana",
            "The Three of Wands symbolizes expansion, exploration, and progress. You have made initial plans, and now you are ready to expand your horizons and explore new opportunities. It indicates that success is on the horizon, but continued effort and patience are required. This card encourages you to have a long-term vision and to trust in your ability to achieve your goals.",
            "When reversed, the Three of Wands may suggest delays, setbacks, or lack of foresight. It can indicate frustration with slow progress or obstacles in your path. The reversal encourages patience and suggests that you may need to re-evaluate your plans or adjust your expectations.",
            listOf("Expansion", "Exploration", "Progress", "Vision", "Opportunity", "Growth"),
            listOf("Delays", "Setbacks", "Frustration", "Lack of foresight", "Obstacles"),
            "card_3_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            17,
            "Four of Wands",
            "Minor arcana",
            "The Four of Wands represents celebration, harmony, and a sense of community. This card suggests that you are experiencing a time of joy and accomplishment, where you can take pride in your achievements and share your happiness with others. It often signifies weddings, reunions, or other events that bring people together to celebrate a milestone or success.",
            "When reversed, the Four of Wands may suggest disharmony, conflict, or a lack of support from others. It can indicate delays or obstacles in plans for celebration or events. The reversal encourages you to focus on resolving conflicts and restoring harmony in your environment.",
            listOf("Celebration", "Harmony", "Community", "Achievement", "Reunion", "Joy"),
            listOf("Disharmony", "Conflict", "Delays", "Obstacles", "Lack of support"),
            "card_4_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            18,
            "Five of Wands",
            "Minor arcana",
            "The Five of Wands represents conflict, competition, and challenges. This card suggests that you are facing disagreements, whether in a team setting or within yourself. It symbolizes a clash of ideas or egos, but it also presents an opportunity to grow and learn from the experience. The card encourages you to find ways to work through the conflict constructively.",
            "When reversed, the Five of Wands may suggest resolution of conflicts, avoiding confrontation, or an unwillingness to engage in challenges. It could indicate that the worst of a disagreement is over, or that you are finding ways to cooperate and compromise. The reversal encourages avoiding unnecessary conflict and seeking peaceful solutions.",
            listOf("Conflict", "Competition", "Challenges", "Disagreements", "Tension", "Growth through struggle"),
            listOf("Resolution", "Avoidance", "Compromise", "Cooperation", "Peaceful solutions"),
            "card_5_of_wands",
            "Fire",
            "no"
        ),
        TarotCard(
            19,
            "Six of Wands",
            "Minor arcana",
            "The Six of Wands signifies victory, recognition, and success. This card suggests that you have overcome challenges and are now receiving the recognition and praise you deserve. It represents public acknowledgment of your accomplishments and encourages you to enjoy the moment of triumph. The Six of Wands reminds you that success comes from perseverance and self-belief.",
            "When reversed, the Six of Wands may suggest a lack of recognition, disappointment, or feelings of failure. It can indicate that you are not receiving the credit you deserve or are experiencing setbacks in your quest for success. The reversal encourages you to reassess your approach and to stay confident despite any delays or obstacles.",
            listOf("Victory", "Recognition", "Success", "Accomplishment", "Public acknowledgment", "Triumph"),
            listOf("Disappointment", "Lack of recognition", "Setbacks", "Failure", "Ego issues"),
            "card_6_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            20,
            "Seven of Wands",
            "Minor arcana",
            "The Seven of Wands represents standing your ground, courage, and defense. This card suggests that you are facing opposition or challenges, but you have the strength and determination to stand firm. It encourages you to protect your position and to be confident in your beliefs, even if others challenge you. The Seven of Wands reminds you to be resilient and stay true to your values.",
            "When reversed, the Seven of Wands may suggest feeling overwhelmed, defenseless, or unable to stand up for yourself. It can indicate that you are struggling to maintain your position or are feeling outnumbered. The reversal encourages you to find inner strength and to not give up in the face of adversity.",
            listOf("Courage", "Defense", "Standing your ground", "Confidence", "Opposition", "Resilience"),
            listOf("Overwhelm", "Defenselessness", "Insecurity", "Feeling outnumbered", "Giving up"),
            "card_7_of_wands",
            "Fire",
            "no"
        ),
        TarotCard(
            21,
            "Eight of Wands",
            "Minor arcana",
            "The Eight of Wands symbolizes speed, progress, and swift movement. This card suggests that things are happening quickly, and momentum is on your side. It represents forward movement, clear communication, and fast-paced energy. The Eight of Wands encourages you to act on your plans and to seize opportunities as they arise, as delays are no longer holding you back.",
            "When reversed, the Eight of Wands may suggest delays, miscommunication, or frustration with slow progress. It can indicate that your plans are being hindered by obstacles or misunderstandings. The reversal encourages patience and suggests that you may need to re-align your efforts before moving forward.",
            listOf("Speed", "Progress", "Movement", "Momentum", "Communication", "Action"),
            listOf("Delays", "Miscommunication", "Frustration", "Obstacles", "Slow progress"),
            "card_8_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            22,
            "Nine of Wands",
            "Minor arcana",
            "The Nine of Wands represents resilience, persistence, and defending what you have built. This card suggests that you are nearing the completion of a challenging situation, but you may feel exhausted or worn down by the effort. It encourages you to stay strong and keep pushing forward, as success is within reach. The Nine of Wands reminds you that your past experiences have made you stronger.",
            "When reversed, the Nine of Wands may suggest burnout, a reluctance to keep fighting, or feeling overwhelmed by ongoing challenges. It could indicate that you are struggling to find the energy to continue or are overly defensive. The reversal encourages you to rest and regain your strength before moving forward.",
            listOf("Resilience", "Persistence", "Defending", "Strength", "Courage", "Near completion"),
            listOf("Burnout", "Exhaustion", "Overwhelm", "Defensiveness", "Struggle to continue"),
            "card_9_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            14,
            "Ten of Wands",
            "Minor arcana",
            "The Ten of Wands represents burdens, responsibilities, and a heavy load. This card suggests that you are carrying too much on your shoulders, feeling overwhelmed by obligations and the demands of daily life. It encourages you to assess your priorities and consider delegating some responsibilities to lighten your load. The Ten of Wands reminds you that it's okay to ask for help and to focus on what truly matters.",
            "When reversed, the Ten of Wands may suggest the release of burdens, relief from responsibilities, or the need to reevaluate your commitments. It can indicate that you are learning to let go of what no longer serves you, which can lead to a sense of freedom. The reversal encourages you to simplify your life and to prioritize self-care.",
            listOf("Burdens", "Responsibilities", "Overwhelm", "Heavy load", "Obligations", "Assessment"),
            listOf("Release of burdens", "Relief", "Letting go", "Simplification", "Prioritization"),
            "card_10_of_wands",
            "Fire",
            "no"
        ),
        TarotCard(
            24,
            "Knight of Wands",
            "Minor arcana",
            "The Knight of Wands signifies energy, passion, and adventure. This card represents a person who is enthusiastic and eager to take action. It encourages you to pursue your passions and embrace new experiences with confidence and courage.",
            "In its reversed position, the Knight of Wands may indicate impulsiveness, recklessness, or a lack of direction. It suggests that you might be rushing into things without fully considering the consequences. The reversal calls for a more thoughtful approach to your actions.",
            listOf("Energy", "Passion", "Adventure", "Courage", "Confidence"),
            listOf("Impulsiveness", "Recklessness", "Lack of direction"),
            "card_knight_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            25,
            "Queen of Wands",
            "Minor arcana",
            "The Queen of Wands embodies confidence, determination, and creativity. She represents a strong, charismatic individual who inspires others. This card encourages you to embrace your inner strength and pursue your goals with passion and enthusiasm.",
            "Reversed, the Queen of Wands may indicate insecurity, jealousy, or a lack of confidence. It suggests that you might be struggling to express your true self or feel overshadowed by others. The reversal calls for self-reflection and the need to reclaim your power.",
            listOf("Confidence", "Determination", "Creativity", "Charisma", "Inspiration"),
            listOf("Insecurity", "Jealousy", "Lack of confidence"),
            "card_queen_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            26,
            "Page of Wands",
            "Minor arcana",
            "The Page of Wands represents enthusiasm, exploration, and new ideas. This card signifies a time of inspiration and creativity. It encourages you to embrace your curiosity and pursue your passions with a sense of adventure.",
            "In its reversed position, the Page of Wands can indicate a lack of direction, hesitation, or feeling uninspired. It suggests that you might be struggling to find your creative spark or feeling blocked in your endeavors. The reversal calls for a renewed focus on your goals.",
            listOf("Enthusiasm", "Exploration", "New ideas", "Creativity", "Adventure"),
            listOf("Lack of direction", "Hesitation", "Feeling uninspired"),
            "card_page_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            27,
            "King of Wands",
            "Minor arcana",
            "The King of Wands symbolizes leadership, vision, and charisma. This card represents a person who is confident and influential, inspiring others with their passion and enthusiasm. It encourages you to embrace your leadership qualities and to pursue your ambitions with courage and determination.",
            "When reversed, the King of Wands may indicate overbearing behavior, impatience, or a lack of vision. It suggests that you might be struggling to find direction or that your leadership style could be causing conflicts. The reversal calls for self-awareness and a more collaborative approach.",
            listOf("Leadership", "Vision", "Charisma", "Influence", "Determination"),
            listOf("Overbearing behavior", "Impatience", "Lack of vision"),
            "card_king_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            28,
            "Ace of Wands",
            "Minor arcana",
            "The Ace of Wands represents new beginnings, inspiration, and creative potential. This card signifies the spark of an idea or the start of a new venture. It encourages you to embrace your creativity and take action towards your passions.",
            "In its reversed position, the Ace of Wands can indicate a lack of inspiration, missed opportunities, or creative blocks. It suggests that you might be feeling stagnant or uncertain about your direction. The reversal encourages you to reignite your passion and explore new possibilities.",
            listOf("New beginnings", "Inspiration", "Creative potential", "Passion", "Action"),
            listOf("Lack of inspiration", "Missed opportunities", "Creative blocks"),
            "card_ace_of_wands",
            "Fire",
            "yes"
        ),
        TarotCard(
            29,
            "Ace of Swords",
            "Minor arcana",
            "The Ace of Swords represents clarity, truth, and new beginnings in the realm of thought and communication. This card signifies a breakthrough or a moment of insight, encouraging you to embrace your intellect and make decisions with confidence.",
            "In its reversed position, the Ace of Swords may indicate confusion, a lack of clarity, or miscommunication. It suggests that you might be struggling to express your thoughts or that you're facing mental challenges. The reversal calls for a deeper exploration of your ideas and communication styles.",
            listOf("Clarity", "Truth", "New ideas", "Intellect", "Decision-making"),
            listOf("Confusion", "Lack of clarity", "Miscommunication"),
            "card_ace_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            30,
            "Two of Swords",
            "Minor arcana",
            "The Two of Swords signifies a stalemate or a difficult decision. This card represents a situation where you may be avoiding conflict or struggling to choose between two options. It encourages you to confront your feelings and make a choice to move forward.",
            "In its reversed position, the Two of Swords can indicate confusion, indecision, or feeling overwhelmed by choices. It suggests that you might be avoiding the truth or failing to see the bigger picture. The reversal calls for clarity and the courage to face your emotions.",
            listOf("Stalemate", "Indecision", "Avoidance", "Difficult choices"),
            listOf("Confusion", "Avoidance", "Overwhelmed by choices"),
            "card_2_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            31,
            "Three of Swords",
            "Minor arcana",
            "The Three of Swords symbolizes heartbreak, grief, and emotional pain. This card indicates a time of sorrow or loss, suggesting that you may need to process your feelings and allow yourself to heal.",
            "In its reversed position, the Three of Swords can indicate healing, forgiveness, or the release of pain. It suggests that you may be moving past a difficult emotional experience and finding ways to let go and rebuild.",
            listOf("Heartbreak", "Grief", "Emotional pain", "Healing"),
            listOf("Avoiding pain", "Holding onto grief"),
            "card_3_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            32,
            "Four of Swords",
            "Minor arcana",
            "The Four of Swords represents rest, contemplation, and recovery. This card signifies a need for a break or a moment of solitude to recharge your mind and spirit. It encourages you to take time for self-reflection and healing.",
            "When reversed, the Four of Swords may indicate restlessness, burnout, or an inability to take a break. It suggests that you might be neglecting your need for rest or struggling to find peace. The reversal calls for prioritizing self-care and mental well-being.",
            listOf("Rest", "Contemplation", "Recovery", "Self-reflection"),
            listOf("Restlessness", "Burnout", "Inability to rest"),
            "card_4_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            33,
            "Five of Swords",
            "Minor arcana",
            "The Five of Swords symbolizes conflict, betrayal, and tension. This card suggests a situation where winning may come at a cost or where disagreements lead to emotional strain. It encourages you to consider the impact of your actions on others.",
            "In its reversed position, the Five of Swords can indicate resolution, forgiveness, or a desire to move past conflict. It suggests that you may be seeking peace and healing after a challenging situation, emphasizing the importance of reconciliation.",
            listOf("Conflict", "Betrayal", "Tension", "Emotional strain"),
            listOf("Resolution", "Forgiveness", "Desire for peace"),
            "card_5_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            34,
            "Six of Swords",
            "Minor arcana",
            "The Six of Swords signifies transition, movement, and travel. This card represents a journey toward calmer waters or a shift away from difficulties. It encourages you to embrace change and seek a better future.",
            "When reversed, the Six of Swords may indicate a feeling of being stuck, resistance to change, or difficulty moving on from a situation. It suggests that you might be struggling to let go of the past or to accept necessary changes. The reversal calls for a willingness to confront your challenges.",
            listOf("Transition", "Movement", "Travel", "Change"),
            listOf("Feeling stuck", "Resistance to change", "Difficulty moving on"),
            "card_6_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            35,
            "Seven of Swords",
            "Minor arcana",
            "The Seven of Swords symbolizes deception, strategy, and cunning. This card suggests a situation where you may need to be discreet or strategic in your approach. It encourages you to consider your options carefully and to be aware of potential dishonesty.",
            "In its reversed position, the Seven of Swords can indicate honesty, accountability, and the need to confront truth. It suggests that you may be ready to face the consequences of your actions and to seek authenticity in your relationships.",
            listOf("Deception", "Strategy", "Cunning", "Discretion"),
            listOf("Dishonesty", "Avoiding accountability", "Lack of authenticity"),
            "card_7_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            36,
            "Eight of Swords",
            "Minor arcana",
            "The Eight of Swords represents restriction, limitation, and feeling trapped. This card indicates a situation where you may feel powerless or unable to move forward. It encourages you to challenge your beliefs and perceptions to find a way out.",
            "In its reversed position, the Eight of Swords can indicate liberation, empowerment, and breaking free from constraints. It suggests that you may be overcoming obstacles and gaining clarity in your situation, allowing for personal growth.",
            listOf("Restriction", "Limitation", "Feeling trapped", "Powerlessness"),
            listOf("Liberation", "Empowerment", "Breaking free"),
            "card_8_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            37,
            "Nine of Swords",
            "Minor arcana",
            "The Nine of Swords symbolizes anxiety, fear, and nightmares. This card indicates a time of mental distress or worry, suggesting that you may be facing challenges that keep you up at night. It encourages you to confront your fears and seek support.",
            "When reversed, the Nine of Swords may indicate a release of anxiety, recovery from fear, or finding peace of mind. It suggests that you may be overcoming your worries and learning to manage your thoughts more effectively.",
            listOf("Anxiety", "Fear", "Nightmares", "Mental distress"),
            listOf("Release of anxiety", "Recovery from fear", "Peace of mind"),
            "card_9_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            38,
            "Ten of Swords",
            "Minor arcana",
            "The Ten of Swords signifies betrayal, loss, and endings. This card represents a situation where you may feel defeated or overwhelmed by circumstances. It encourages you to acknowledge your pain and to understand that endings can lead to new beginnings.",
            "In its reversed position, the Ten of Swords can indicate recovery, resilience, and the potential for new beginnings. It suggests that you may be moving past a difficult experience and finding ways to heal and rebuild your life.",
            listOf("Betrayal", "Loss", "Endings", "Defeat"),
            listOf("Recovery", "Resilience", "New beginnings"),
            "card_10_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            39,
            "Page of Swords",
            "Minor arcana",
            "The Page of Swords represents curiosity, intelligence, and communication. This card suggests a time of exploration and learning, encouraging you to ask questions and seek knowledge. It embodies a youthful and inquisitive energy.",
            "In its reversed position, the Page of Swords may indicate deceit, gossip, or a lack of clarity in communication. It suggests that you might be facing challenges in expressing yourself or dealing with misunderstandings.",
            listOf("Curiosity", "Intelligence", "Communication", "Exploration"),
            listOf("Deceit", "Gossip", "Lack of clarity"),
            "card_page_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            40,
            "Knight of Swords",
            "Minor arcana",
            "The Knight of Swords symbolizes action, determination, and ambition. This card suggests a time of swift movement and decisiveness, encouraging you to pursue your goals with passion and energy. It embodies a courageous and assertive attitude.",
            "When reversed, the Knight of Swords may indicate recklessness, impatience, or a lack of direction. It suggests that you might be rushing into situations without fully considering the consequences, calling for a more balanced approach.",
            listOf("Action", "Determination", "Ambition", "Decisiveness"),
            listOf("Recklessness", "Impatience", "Lack of direction"),
            "card_knight_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            41,
            "Queen of Swords",
            "Minor arcana",
            "The Queen of Swords represents intellect, independence, and clear communication. This card signifies a person who values truth and clarity, encouraging you to trust your intuition and express your thoughts honestly. She embodies wisdom and strength.",
            "In its reversed position, the Queen of Swords may indicate emotional coldness, bitterness, or a lack of clarity in communication. It suggests that you might be struggling with your emotions or finding it difficult to connect with others. The reversal calls for empathy and open-heartedness.",
            listOf("Intellect", "Independence", "Clear communication", "Wisdom"),
            listOf("Emotional coldness", "Bitterness", "Lack of clarity"),
            "card_queen_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            42,
            "King of Swords",
            "Minor arcana",
            "The King of Swords symbolizes authority, intellect, and decision-making. This card signifies a strong and analytical leader who values logic and fairness. It encourages you to embrace your intellectual strengths and to lead with clarity and integrity.",
            "When reversed, the King of Swords may indicate tyranny, manipulation, or misuse of power. It suggests that you might be struggling with authority or dealing with someone who is not using their intellect for the greater good. The reversal calls for ethical considerations in your actions.",
            listOf("Authority", "Intellect", "Decision-making", "Fairness"),
            listOf("Tyranny", "Manipulation", "Misuse of power"),
            "card_king_of_swords",
            "Air",
            "no"
        ),
        TarotCard(
            43,
            "Ace of Cups",
            "Minor arcana",
            "The Ace of Cups represents new emotional beginnings, love, and spiritual fulfillment. This card signifies the start of a new relationship or emotional journey, encouraging you to open your heart and embrace joy and connection.",
            "In its reversed position, the Ace of Cups may indicate emotional blockages, repressed feelings, or a lack of self-love. It suggests that you might be struggling to express your emotions or facing challenges in your relationships. The reversal calls for healing and self-acceptance.",
            listOf("New beginnings", "Love", "Emotional fulfillment", "Joy"),
            listOf("Emotional blockages", "Repressed feelings", "Lack of self-love"),
            "card_ace_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            44,
            "Two of Cups",
            "Minor arcana",
            "The Two of Cups symbolizes partnership, connection, and mutual respect. This card signifies a strong bond between individuals, suggesting harmony and understanding in relationships. It encourages you to nurture your connections and find balance in love.",
            "In its reversed position, the Two of Cups can indicate imbalance, disconnection, or unresolved issues in a partnership. It suggests that you may be experiencing difficulties in a relationship and need to address any underlying tensions to restore harmony.",
            listOf("Partnership", "Connection", "Mutual respect", "Harmony"),
            listOf("Imbalance", "Disconnection", "Unresolved issues"),
            "card_2_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            45,
            "Three of Cups",
            "Minor arcana",
            "The Three of Cups represents celebration, friendship, and community. This card signifies joyous gatherings and shared experiences, encouraging you to connect with loved ones and celebrate the positive aspects of life.",
            "In its reversed position, the Three of Cups may indicate isolation, overindulgence, or a lack of support from friends. It suggests that you might be feeling disconnected from your social circle and need to reestablish those connections. The reversal calls for balance in social activities.",
            listOf("Celebration", "Friendship", "Community", "Joy"),
            listOf("Isolation", "Overindulgence", "Lack of support"),
            "card_3_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            46,
            "Four of Cups",
            "Minor arcana",
            "The Four of Cups symbolizes contemplation, apathy, and reevaluation. This card indicates a time of introspection, suggesting that you may be feeling unfulfilled or disconnected from your current emotional state. It encourages you to explore new perspectives and opportunities.",
            "When reversed, the Four of Cups can indicate openness to new experiences, acceptance, and renewed interest in life. It suggests that you may be ready to embrace new emotional opportunities and engage with your surroundings more actively.",
            listOf("Contemplation", "Apathy", "Reevaluation", "Introspection"),
            listOf("Openness", "Acceptance", "Renewed interest"),
            "card_4_of_cups",
            "Water",
            "maybe"
        ),
        TarotCard(
            47,
            "Five of Cups",
            "Minor arcana",
            "The Five of Cups represents grief, loss, and disappointment. This card signifies a period of mourning or emotional turmoil, encouraging you to acknowledge your feelings and seek healing. It suggests focusing on what remains rather than what has been lost.",
            "In its reversed position, the Five of Cups can indicate acceptance, forgiveness, and moving on from grief. It suggests that you may be ready to let go of past hurts and embrace new opportunities for emotional fulfillment.",
            listOf("Grief", "Loss", "Disappointment", "Emotional turmoil"),
            listOf("Acceptance", "Forgiveness", "Moving on"),
            "card_5_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            48,
            "Six of Cups",
            "Minor arcana",
            "The Six of Cups symbolizes nostalgia, childhood memories, and innocence. This card indicates a time of reflection on past experiences, encouraging you to reconnect with your inner child and embrace joyful memories.",
            "When reversed, the Six of Cups may indicate a reluctance to let go of the past or an inability to move forward. It suggests that you might be stuck in old patterns or memories, preventing you from enjoying the present. The reversal calls for healing and growth.",
            listOf("Nostalgia", "Childhood memories", "Innocence", "Joyful reflection"),
            listOf("Reluctance to let go", "Inability to move forward", "Stuck in the past"),
            "card_6_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            49,
            "Seven of Cups",
            "Minor arcana",
            "The Seven of Cups represents choices, dreams, and illusions. This card suggests a time of exploration and imagination, encouraging you to consider your options and dreams. However, it warns against getting lost in fantasies and urges clarity in decision-making.",
            "In its reversed position, the Seven of Cups may indicate clarity, focus, and the ability to make decisions. It suggests that you may be overcoming confusion and gaining a better understanding of your priorities and desires.",
            listOf("Choices", "Dreams", "Illusions", "Exploration"),
            listOf("Clarity", "Focus", "Decision-making"),
            "card_7_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            50,
            "Eight of Cups",
            "Minor arcana",
            "The Eight of Cups symbolizes withdrawal, introspection, and the search for deeper meaning. This card signifies a decision to leave behind unfulfilling situations and seek emotional fulfillment elsewhere. It encourages you to honor your feelings and pursue what truly matters to you.",
            "When reversed, the Eight of Cups may indicate stagnation, fear of change, or an inability to let go of the past. It suggests that you might be struggling to move on from a situation that no longer serves you. The reversal calls for courage to confront your emotions and seek growth.",
            listOf("Withdrawal", "Introspection", "Search for meaning", "Emotional fulfillment"),
            listOf("Stagnation", "Fear of change", "Inability to let go"),
            "card_8_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            51,
            "Nine of Cups",
            "Minor arcana",
            "The Nine of Cups represents emotional satisfaction, contentment, and wishes fulfilled. This card signifies a time of joy and gratitude, encouraging you to appreciate the abundance in your life and to celebrate your achievements.",
            "In its reversed position, the Nine of Cups may indicate dissatisfaction, overindulgence, or unmet desires. It suggests that you might be feeling unfulfilled despite appearances and encourages you to explore what truly brings you happiness.",
            listOf("Emotional satisfaction", "Contentment", "Wishes fulfilled", "Joy"),
            listOf("Dissatisfaction", "Overindulgence", "Unmet desires"),
            "card_9_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            52,
            "Ten of Cups",
            "Minor arcana",
            "The Ten of Cups symbolizes happiness, family, and emotional fulfillment. This card signifies a deep sense of joy and harmony in relationships, encouraging you to cherish the connections that bring you love and support.",
            "In its reversed position, the Ten of Cups may indicate disharmony, broken relationships, or a lack of emotional fulfillment. It suggests that you might be facing challenges in your family or relationships, calling for open communication and healing.",
            listOf("Happiness", "Family", "Emotional fulfillment", "Joyful connections"),
            listOf("Disharmony", "Broken relationships", "Lack of fulfillment"),
            "card_10_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            53,
            "Page of Cups",
            "Minor arcana",
            "The Page of Cups represents creativity, intuition, and emotional exploration. This card signifies a time of new emotional experiences and artistic inspiration, encouraging you to embrace your feelings and express yourself authentically.",
            "In its reversed position, the Page of Cups may indicate emotional immaturity, unrealistic expectations, or a lack of creativity. It suggests that you might be struggling to connect with your emotions or facing challenges in expressing your true self.",
            listOf("Creativity", "Intuition", "Emotional exploration", "Authenticity"),
            listOf("Emotional immaturity", "Unrealistic expectations", "Lack of creativity"),
            "card_page_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            54,
            "Knight of Cups",
            "Minor arcana",
            "The Knight of Cups symbolizes romance, idealism, and emotional pursuit. This card signifies a time of following your heart and embracing your passions, encouraging you to take action in matters of love and creativity.",
            "In its reversed position, the Knight of Cups may indicate moodiness, unrequited love, or unrealistic ideals. It suggests that you might be struggling with emotional instability or facing challenges in romantic relationships. The reversal calls for grounding your emotions.",
            listOf("Romance", "Idealism", "Emotional pursuit", "Passion"),
            listOf("Moodiness", "Unrequited love", "Unrealistic ideals"),
            "card_knight_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            55,
            "Queen of Cups",
            "Minor arcana",
            "The Queen of Cups represents compassion, intuition, and emotional security. This card signifies a nurturing and empathetic individual, encouraging you to trust your feelings and offer support to those around you.",
            "When reversed, the Queen of Cups may indicate emotional instability, insecurity, or dependency. It suggests that you might be struggling with your emotions or feeling overwhelmed. The reversal calls for self-care and healthy boundaries.",
            listOf("Compassion", "Intuition", "Emotional security", "Nurturing"),
            listOf("Emotional instability", "Insecurity", "Dependency"),
            "card_queen_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            56,
            "King of Cups",
            "Minor arcana",
            "The King of Cups symbolizes emotional maturity, compassion, and balance. This card signifies a wise and caring leader who values emotional intelligence and harmony in relationships. It encourages you to embrace your feelings while remaining grounded.",
            "In its reversed position, the King of Cups may indicate emotional manipulation, moodiness, or lack of control over feelings. It suggests that you might be struggling with your emotions or facing challenges in relationships. The reversal calls for honest self-reflection and emotional balance.",
            listOf("Emotional maturity", "Compassion", "Balance", "Wisdom"),
            listOf("Emotional manipulation", "Moodiness", "Lack of control"),
            "card_king_of_cups",
            "Water",
            "yes"
        ),
        TarotCard(
            57,
            "The Fool",
            "Major arcana",
            "The Fool symbolizes new beginnings, spontaneity, and a free spirit. This card encourages you to embrace the unknown and take risks, trusting in the journey ahead.",
            "In its reversed position, The Fool may indicate recklessness or a lack of direction. It suggests that you might be acting impulsively without considering the consequences.",
            listOf("New beginnings", "Adventure", "Spontaneity"),
            listOf("Recklessness", "Naivety", "Poor choices"),
            "card_fool",
            "Air",
            "yes"
        ),
        TarotCard(
            58,
            "The Magician",
            "Major arcana",
            "The Magician represents manifestation, resourcefulness, and power. This card signifies that you have the tools and skills necessary to create your reality and achieve your goals.",
            "In its reversed position, The Magician may indicate manipulation or poor planning. It suggests that you might be misusing your talents or lacking focus.",
            listOf("Manifestation", "Resourcefulness", "Power"),
            listOf("Manipulation", "Lack of focus", "Poor planning"),
            "card_magician",
            "Air",
            "maybe"
        ),
        TarotCard(
            59,
            "The High Priestess",
            "Major arcana",
            "The High Priestess symbolizes intuition, unconscious knowledge, and mystery. This card encourages you to trust your instincts and explore your inner self.",
            "In its reversed position, The High Priestess may indicate secrets or a disconnect from your intuition. It suggests that you might be ignoring your inner voice.",
            listOf("Intuition", "Mystery", "Inner wisdom"),
            listOf("Secrets", "Lack of clarity", "Disconnection"),
            "card_the_high_priestess",
            "Water",
            "maybe"
        ),
        TarotCard(
            60,
            "The Empress",
            "Major arcana",
            "The Empress represents abundance, fertility, and nurturing energy. This card signifies the importance of self-care and creating a loving environment.",
            "In its reversed position, The Empress may indicate neglect or dependence. It suggests that you might be struggling to care for yourself or others.",
            listOf("Abundance", "Nurturing", "Fertility"),
            listOf("Neglect", "Dependence", "Creative block"),
            "card_the_empress",
            "Earth",
            "yes"
        ),
        TarotCard(
            61,
            "The Emperor",
            "Major arcana",
            "The Emperor symbolizes authority, structure, and stability. This card signifies the importance of leadership and taking control of your life.",
            "In its reversed position, The Emperor may indicate tyranny or rigidity. It suggests that you might be struggling with authority or feeling stifled.",
            listOf("Authority", "Structure", "Stability"),
            listOf("Tyranny", "Rigidity", "Inflexibility"),
            "card_the_emperor",
            "Fire",
            "yes"
        ),
        TarotCard(
            62,
            "The Hierophant",
            "Major arcana",
            "The Hierophant represents tradition, spirituality, and conformity. This card encourages you to seek guidance and adhere to established beliefs.",
            "In its reversed position, The Hierophant may indicate rebellion or unconventionality. It suggests that you might be challenging the status quo.",
            listOf("Tradition", "Spiritual guidance", "Conformity"),
            listOf("Rebellion", "Unconventionality", "Questioning beliefs"),
            "card_the_hierophant",
            "Earth",
            "yes"
        ),
        TarotCard(
            63,
            "The Lovers",
            "Major arcana",
            "The Lovers symbolizes love, harmony, and choices. This card signifies meaningful relationships and the importance of making decisions aligned with your values.",
            "In its reversed position, The Lovers may indicate imbalance or miscommunication in relationships. It suggests that you might be struggling with choices or commitment.",
            listOf("Love", "Harmony", "Choices"),
            listOf("Imbalance", "Miscommunication", "Indecision"),
            "card_the_lovers",
            "Air",
            "yes"
        ),
        TarotCard(
            64,
            "The Chariot",
            "Major arcana",
            "The Chariot represents determination, willpower, and triumph over obstacles. This card encourages you to stay focused and control your emotions to achieve success.",
            "In its reversed position, The Chariot may indicate lack of direction or control. It suggests that you might be feeling overwhelmed or unable to maintain your path.",
            listOf("Determination", "Control", "Victory"),
            listOf("Lack of direction", "Overwhelm", "Failure to act"),
            "card_the_chariot",
            "Water",
            "yes"
        ),
        TarotCard(
            65,
            "Strength",
            "Major arcana",
            "Strength symbolizes courage, patience, and inner strength. This card signifies the importance of compassion and understanding in overcoming challenges.",
            "In its reversed position, Strength may indicate weakness or self-doubt. It suggests that you might be struggling with confidence or feeling vulnerable.",
            listOf("Courage", "Patience", "Inner strength"),
            listOf("Weakness", "Self-doubt", "Vulnerability"),
            "card_strength",
            "Fire",
            "yes"
        ),
        TarotCard(
            66,
            "The Hermit",
            "Major arcana",
            "The Hermit represents introspection, solitude, and inner guidance. This card encourages you to take time for self-reflection and seek your truth.",
            "In its reversed position, The Hermit may indicate isolation or withdrawal. It suggests that you might be avoiding social interactions or feeling disconnected.",
            listOf("Introspection", "Solitude", "Inner guidance"),
            listOf("Isolation", "Withdrawal", "Disconnection"),
            "card_the_hermit",
            "Earth",
            "yes"
        ),
        TarotCard(
            67,
            "The Wheel of Fortune",
            "Major arcana",
            "The Wheel of Fortune symbolizes cycles, destiny, and change. This card signifies the ebb and flow of life and encourages you to embrace the inevitable changes.",
            "In its reversed position, The Wheel of Fortune may indicate resistance to change or bad luck. It suggests that you might be struggling to adapt to life's ups and downs.",
            listOf("Cycles", "Destiny", "Change"),
            listOf("Resistance", "Bad luck", "Feeling stuck"),
            "card_the_wheel_of_fortune",
            "Fire",
            "yes"
        ),
        TarotCard(
            68,
            "Justice",
            "Major arcana",
            "Justice represents fairness, truth, and law. This card encourages you to make decisions based on integrity and to seek balance in your actions.",
            "In its reversed position, Justice may indicate dishonesty or imbalance. It suggests that you might be facing unfair treatment or avoiding accountability.",
            listOf("Fairness", "Truth", "Balance"),
            listOf("Dishonesty", "Imbalance", "Avoiding accountability"),
            "card_justice",
            "Air",
            "maybe"
        ),
        TarotCard(
            69,
            "The Hanged Man",
            "Major arcana",
            "The Hanged Man symbolizes surrender, letting go, and new perspectives. This card encourages you to pause and reflect before taking action.",
            "In its reversed position, The Hanged Man may indicate resistance to change or delays. It suggests that you might be struggling to let go or feeling stuck.",
            listOf("Surrender", "New perspectives", "Letting go"),
            listOf("Resistance", "Delays", "Feeling stuck"),
            "card_the_hanged_man",
            "Water",
            "no"
        ),
        TarotCard(
            70,
            "Death",
            "Major arcana",
            "Death symbolizes transformation, endings, and new beginnings. This card signifies the end of a cycle and encourages you to embrace change and renewal.",
            "In its reversed position, Death may indicate resistance to change or stagnation. It suggests that you might be struggling to let go of the past.",
            listOf("Transformation", "Endings", "New beginnings"),
            listOf("Resistance to change", "Stagnation", "Fear of loss"),
            "card_death",
            "Water",
            "no"
        ),
        TarotCard(
            71,
            "Temperance",
            "Major arcana",
            "Temperance symbolizes balance, moderation, and harmony. This card encourages you to find equilibrium in your life and to practice patience and self-control.",
            "In its reversed position, Temperance may indicate imbalance or excess. It suggests that you might be struggling with moderation or feeling overwhelmed.",
            listOf("Balance", "Moderation", "Harmony"),
            listOf("Imbalance", "Excess", "Overwhelm"),
            "card_temperance",
            "Fire",
            "yes"
        ),
        TarotCard(
            72,
            "The Devil",
            "Major arcana",
            "The Devil symbolizes temptation, materialism, and bondage. This card signifies the importance of recognizing unhealthy patterns and breaking free from limitations.",
            "In its reversed position, The Devil may indicate liberation or overcoming addiction. It suggests that you might be finding the strength to break free from negative influences.",
            listOf("Temptation", "Materialism", "Bondage"),
            listOf("Liberation", "Overcoming addiction", "Breaking free"),
            "card_72_the_devil",
            "N/A",
            "yes"
        ),
        TarotCard(
            72,
            "The Devil",
            "Major arcana",
            "The Devil symbolizes temptation, materialism, and bondage. This card signifies the importance of recognizing unhealthy patterns and breaking free from limitations.",
            "In its reversed position, The Devil may indicate liberation or overcoming addiction. It suggests that you might be finding the strength to break free from negative influences.",
            listOf("Temptation", "Materialism", "Bondage"),
            listOf("Liberation", "Overcoming addiction", "Breaking free"),
            "card_the_devil",
            "Earth",
            "no"
        ),
        TarotCard(
            73,
            "The Tower",
            "Major arcana",
            "The Tower symbolizes chaos, upheaval, and revelation. This card signifies the breaking down of old structures to make way for new perspectives and growth.",
            "In its reversed position, The Tower may indicate avoidance of change or fear of disaster. It suggests that you might be resisting necessary upheaval.",
            listOf("Chaos", "Upheaval", "Revelation"),
            listOf("Avoidance", "Fear of disaster", "Resistance"),
            "card_the_tower",
            "Fire",
            "no"
        ),
        TarotCard(
            74,
            "The Star",
            "Major arcana",
            "The Star symbolizes hope, inspiration, and renewal. This card signifies the importance of optimism and faith in the future.",
            "In its reversed position, The Star may indicate despair or a lack of faith. It suggests that you might be feeling hopeless or disconnected from your dreams.",
            listOf("Hope", "Inspiration", "Renewal"),
            listOf("Despair", "Lack of faith", "Disconnection"),
            "card_the_star",
            "Air",
            "yes"
        ),
        TarotCard(
            75,
            "The Moon",
            "Major arcana",
            "The Moon symbolizes intuition, dreams, and the subconscious. This card encourages you to explore your inner world and pay attention to your feelings and instincts.",
            "In its reversed position, The Moon may indicate confusion or deception. It suggests that you might be struggling to see things clearly or facing illusions.",
            listOf("Intuition", "Dreams", "Subconscious"),
            listOf("Confusion", "Deception", "Illusions"),
            "card_the_moon",
            "Air",
            "yes"
        ),
        TarotCard(
            76,
            "The Sun",
            "Major arcana",
            "The Sun symbolizes joy, success, and positivity. This card signifies abundance and the importance of embracing life with enthusiasm and confidence.",
            "In its reversed position, The Sun may indicate temporary setbacks or negativity. It suggests that you might be struggling to find joy or feeling uninspired.",
            listOf("Joy", "Success", "Positivity"),
            listOf("Setbacks", "Negativity", "Lack of inspiration"),
            "card_the_sun",
            "Air",
            "yes"
        ),
        TarotCard(
            77,
            "Judgment",
            "Major arcana",
            "Judgment symbolizes reflection, reckoning, and inner calling. This card encourages you to evaluate your past and make decisions that align with your true self.",
            "In its reversed position, Judgment may indicate self-doubt or refusal to change. It suggests that you might be avoiding necessary self-reflection.",
            listOf("Reflection", "Reckoning", "Inner calling"),
            listOf("Self-doubt", "Refusal to change", "Avoidance"),
            "card_judgment",
            "Fire",
            "no"
        ),
        TarotCard(
            78,
            "The World",
            "Major arcana",
            "The World symbolizes completion, fulfillment, and achievement. This card signifies the successful conclusion of a cycle and the realization of your goals.",
            "In its reversed position, The World may indicate incompletion or stagnation. It suggests that you might be feeling unfulfilled or lacking closure.",
            listOf("Completion", "Fulfillment", "Achievement"),
            listOf("Incompletion", "Stagnation", "Lack of closure"),
            "card_the_world",
            "Fire",
            "yes"
        )
    )

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    suspend fun getCardOfDay(): CardOfDay {
        val today = getCurrentDate()
        val storedDate = context.dataStore.data.map { it[dateKey] }.first()
        val storedCardId = context.dataStore.data.map { it[cardOfDayKey] }.first()
        val storedOrientation = context.dataStore.data.map { it[cardOrientationKey] }.first()

        return if (storedDate == today && storedCardId != null && storedOrientation != null) {
            CardOfDay(
                card = cards.first { it.id.toString() == storedCardId },
                isUpright = storedOrientation.toBoolean()
            )
        } else {
            val randomCard = cards.random()
            val isUpright = Random.nextBoolean()
            context.dataStore.edit { prefs ->
                prefs[dateKey] = today
                prefs[cardOfDayKey] = randomCard.id.toString()
                prefs[cardOrientationKey] = isUpright.toString()
            }
            CardOfDay(randomCard, isUpright)
        }
    }

    fun getCardById(id: Int): TarotCard {
        return cards.first { it.id == id }
    }
    fun getRandomCard(): CardOfDay {
        val randomCard = cards.random()
        val isUpright = Random.nextBoolean()
        Log.d("CardRepository", "Random card: ${randomCard.name}, isUpright: $isUpright")
        return CardOfDay(randomCard, isUpright)
    }
}